package com.example.shorturl.Utils;


import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FormatValidation {
    /**
     * 邮箱正则
     */
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    /**
     * 验证码正则, 6位数字或字母
     */
    public static final String VERIFY_CODE_REGEX = "^[a-zA-Z\\d]{6}$";


    /**
     * 校验邮箱格式
     * @param email 邮箱
     * @return 校验结果
     */
    public boolean isValidEmail(String email){
        if(StrUtil.isBlank(email)){
            return false;
        }
       return email.matches(EMAIL_REGEX);
    }

    /**
     * 校验验证码格式
     * @param code 验证码
     * @return 校验结果
     */
    public boolean isValidCode(String code){
        if(StrUtil.isBlank(code)){
            return false;
        }
        return code.matches(VERIFY_CODE_REGEX);
    }

}
