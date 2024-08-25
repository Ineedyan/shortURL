package com.example.shorturl.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shorturl.DTO.LoginFormDTO;
import com.example.shorturl.DTO.UserInfoDTO;
import com.example.shorturl.Entity.User;
import com.example.shorturl.Mapper.UserMapper;
import com.example.shorturl.Service.EmailService;
import com.example.shorturl.Service.UserService;
import com.example.shorturl.Utils.FormatValidation;
import com.example.shorturl.Utils.RedisConstants;
import com.example.shorturl.Utils.Result;
import com.example.shorturl.Utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.shorturl.Utils.RedisConstants.*;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private EmailService emailService;

    @Resource
    private FormatValidation formatValidation;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 登录功能
     * @param loginFormDTO 携带登录信息（用户名 + 验证码 || 用户名 + 密码）
     * @return 登录结果
     */
    @Override
    public Result login(LoginFormDTO loginFormDTO) {
        // 获取账户信息
        String username = loginFormDTO.getUsername();
        User user = new User();
        // 对账户进行格式校验，要求：为邮箱格式
        if(!formatValidation.isValidEmail(username)){
            return Result.fail("邮箱格式不正确！请检查输入！");
        }
        // 获取登录类型
        Byte loginType = loginFormDTO.getLoginType();
        // 根据登录类型进行登录
        if(loginType == 0){
            // 邮箱 + 验证码 登录
            String code = loginFormDTO.getCode();
            // 校验验证码
            if(!formatValidation.isValidCode(code)){
                return Result.fail("验证码格式不正确！请检查输入！");
            }
            // 从Redis中取出验证码
            String redisCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + username);
            if(StrUtil.isBlank(redisCode) || !redisCode.equals(code)){
                return Result.fail("验证码错误！请重新输入！");
            }
            user = query().eq("username", username).one();
            if(user == null){
                // 注册一个账号
                user = createUserByEmail(username);
            }
        }else if(loginType == 1){
            // 邮箱 + 密码 登录
            String password = loginFormDTO.getPassword();
            user = query().eq("username", username).one();
            // 用户不存在
            if(user == null){
                return Result.fail("用户或密码错误！");
            }
            // 对密码进行加密比对
            String salt = user.getSalt();
            String pw = BCrypt.hashpw(password, salt);
            // 不匹配，登录失败
            if(!pw.equals(user.getPassword())){
                return Result.fail("用户或密码错误！");
            }
        }
        UserInfoDTO userInfoDTO = BeanUtil.copyProperties(user, UserInfoDTO.class);
        // token
        String token = UUID.randomUUID().toString();
        // Token存入Redis
        String userMap = JSONUtil.toJsonStr(userInfoDTO);
        stringRedisTemplate.opsForValue().set(LOGIN_TOKEN_KEY + token, userMap ,LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        // 返回
        return Result.ok(token);
    }

    /**
     * 发送验证码
     * @param username 用户邮箱
     * @return 返回结果
     */
    @Override
    public Result sendCode(String username) {
        // 校验
        if(!formatValidation.isValidEmail(username)){
            return Result.fail("邮箱格式不正确！请检查输入！");
        }
        // 生成验证码
        String code = RandomUtil.randomNumbers(6);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + username, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        try {
            emailService.sendCodeForLogin(username, code);
        } catch (Exception e) {
            return Result.fail("发送验证码失败！请稍候重试！");
        }
        return Result.ok("发送验证码成功，请前往邮箱查收");
    }

    /**
     * 实现签到功能
     * @return 签到结果
     */
    @Override
    public Result sign() {
        // 获取用户信息
        Long userId = UserHolder.getUser().getId();
        // 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        int day = now.getDayOfMonth() - 1;
        stringRedisTemplate.opsForValue().setBit(key, day, true);
        return Result.ok("用户" + userId + "签到成功！");
    }

    /**
     * 统计连续签到
     * @return
     */
    @Override
    public Result signCount() {
        // 获取用户信息
        Long userId = UserHolder.getUser().getId();
        // 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 今天是几号
        int day = now.getDayOfMonth();
        // 获取截至今日的签到记录
        List<Long> res = stringRedisTemplate.opsForValue().bitField(key,
                BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(day)).valueAt(0));

        if(res == null || res.isEmpty()){
            return Result.ok(0);
        }
        Long num = res.get(0);
        if(num == null || num == 0){
            return Result.ok(0);
        }
        int count = 0;
        while(true){
            if((num & 1) == 0){
                break;
            }else{
                count ++;
            }
            num >>>= 1;
        }
        return Result.ok(count);
    }


    /**
     * 创建一个账户
     * @param username 账户名
     * @return 创建结果
     */
    private User createUserByEmail(String username) {
        User user = new User();
        user.setUsername(username);
        user.setNickName("user" + RandomUtil.randomString(5));
        user.setCreateTime(LocalDateTime.now());
        save(user);
        return user;
    }
}
