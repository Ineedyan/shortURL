package com.example.shorturl.Expection;

/**
 * 异常：接口访问限制
 */
public class AccessLimitException extends RuntimeException{

    public AccessLimitException(String message) {
        super(message);
    }

}
