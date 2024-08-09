package com.example.shorturl.Expection;

import com.example.shorturl.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 处理接口访问异常
    @ExceptionHandler(AccessLimitException.class)
    public ResponseEntity<Result> handleAccessLimitException(AccessLimitException ex){
        Result result = Result.fail(ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.TOO_MANY_REQUESTS);
    }

    // 未登录异常
    @ExceptionHandler(NoLoginException.class)
    public ResponseEntity<Result> handleNoLoginException(NoLoginException ex){
        Result result = Result.fail(ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    // 处理所有未处理的异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handleException(Exception ex) {
        Result result = Result.fail("服务器繁忙，请稍后重试！");
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
