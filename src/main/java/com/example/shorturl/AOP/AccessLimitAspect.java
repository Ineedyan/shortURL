package com.example.shorturl.AOP;

import com.example.shorturl.AOP.Annotation.AccessLimit;
import com.example.shorturl.Enum.LimitType;
import com.example.shorturl.Expection.AccessLimitException;
import com.example.shorturl.Utils.IpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;


@Aspect
@Component
@Slf4j
public class AccessLimitAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> limitScript;
    static {
        limitScript = new DefaultRedisScript<>();
        limitScript.setLocation(new ClassPathResource("lua/accessLimit.lua"));
        limitScript.setResultType(Long.class);
    }

    /**
     * 注解切入
     * @param joinPoint 切入点
     * @param accessLimit 注解
     * @throws Throwable 异常
     */
    @Before("@annotation(accessLimit)")
    public void before(JoinPoint joinPoint, AccessLimit accessLimit) throws Throwable {
        // 获取注解信息
        String prefix = accessLimit.keyPrefix();
        long second = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();
        LimitType type = accessLimit.limitType();
        // 生成key
        String key = getKey(accessLimit, joinPoint);
        List<String> keys = Collections.singletonList(key);
        // 接口访问限制
        Long number = stringRedisTemplate.execute(limitScript, keys, String.valueOf(maxCount), String.valueOf(second));
        if (number == null || number.intValue() > maxCount) {
            throw new AccessLimitException("请求过于频繁，请稍后重试！");
        }

    }
    /**
     * 获取分布式锁的Key
     * @param accessLimit 注解
     * @param joinPoint 切入点
     * @return 锁键
     */
    public String getKey(AccessLimit accessLimit, JoinPoint joinPoint){
        StringBuffer sb = new StringBuffer(accessLimit.keyPrefix());
        if(accessLimit.limitType() == LimitType.IP){
            String ip = IpUtil.getIp(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest());
            sb.append(ip).append("-");
            log.debug(ip);
        }
        // 方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String methodName = method.getName();
        sb.append(methodName);
        return sb.toString();

    }
}

