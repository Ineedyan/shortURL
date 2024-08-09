package com.example.shorturl.Controller;

import com.example.shorturl.AOP.Annotation.AccessLimit;
import com.example.shorturl.DTO.LoginFormDTO;
import com.example.shorturl.DTO.UserInfoDTO;
import com.example.shorturl.Enum.LimitType;
import com.example.shorturl.Service.UserService;
import com.example.shorturl.Utils.Result;
import com.example.shorturl.Utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 账户登录
     * @param loginFormDTO 登录信息（用户名 + 验证码 || 用户名 + 密码）
     * @return 登录结果
     */
    @AccessLimit(seconds = 600, maxCount = 10, limitType = LimitType.IP)
    @PostMapping("login")
    public Result login(@RequestBody LoginFormDTO loginFormDTO){
        return userService.login(loginFormDTO);
    }

    /**
     * 发送验证码
     * @param username 用户名
     * @return 发送结果
     */
    @AccessLimit(seconds = 60, maxCount = 1, limitType = LimitType.IP)
    @GetMapping("sendCode")
    public Result sendCode(@RequestParam("username") String username){
        return userService.sendCode(username);
    }


    /**
     * 获取用户个人资料信息
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public Result getInfo(){
        return Result.ok(UserHolder.getUser());
    }




}
