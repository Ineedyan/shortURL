package com.example.shorturl.Service.impl;

import cn.hutool.extra.template.TemplateEngine;
import com.example.shorturl.Service.EmailService;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private  String mailUsername;
    @Resource
    private JavaMailSender javaMailSender;

    @Override
    public void sendCodeForLogin(String username, String code) {
        // 发送验证码到对象邮箱中
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            // 设置邮件主题
            mimeMessageHelper.setFrom(mailUsername);
            // 设置邮件接收者
            mimeMessageHelper.setTo(username);
            // 设置发送日期
            mimeMessageHelper.setSentDate(new Date());
            // 设置邮件主题
            mimeMessageHelper.setSubject("验证码");
            // 设置邮件内容
            mimeMessageHelper.setText("您的验证码是：" + code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        javaMailSender.send(mimeMessage);
    }
}
