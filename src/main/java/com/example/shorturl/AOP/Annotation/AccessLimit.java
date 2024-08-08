package com.example.shorturl.AOP.Annotation;

import com.example.shorturl.Enum.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    /**
     * 时间范围，表示60s内访问的次数
     */
    long seconds() default 60;

    /**
     * 最大请求数量限制
     */
    int maxCount();


    /**
     * 锁的前缀
     */
    String keyPrefix() default "accessLimit:";

    /**
     *
     */
    LimitType limitType() default LimitType.DEFAULT;
}
