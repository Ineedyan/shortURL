package com.example.shorturl.Interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.shorturl.DTO.UserInfoDTO;
import com.example.shorturl.Entity.User;
import com.example.shorturl.Utils.RedisConstants;
import com.example.shorturl.Utils.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.shorturl.Utils.RedisConstants.LOGIN_TOKEN_KEY;
import static com.example.shorturl.Utils.RedisConstants.LOGIN_TOKEN_TTL;

/**
 * 刷新Token
 */
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取Token
        String token = request.getHeader("auth-token");
        // token空，放行
        if(StrUtil.isBlank(token)){
            return true;
        }
        // 获取Redis用户
        String userStr = stringRedisTemplate.opsForValue().get(LOGIN_TOKEN_KEY + token);
        if(StrUtil.isBlank(userStr)){
            return true;
        }
        UserInfoDTO userInfoDTO = JSONUtil.toBean(userStr, UserInfoDTO.class);
        // 存入ThreadLocal
        UserHolder.saveUser(userInfoDTO);
        // 刷新Token
        stringRedisTemplate.expire(LOGIN_TOKEN_KEY + token, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
