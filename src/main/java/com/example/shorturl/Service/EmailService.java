package com.example.shorturl.Service;

import org.springframework.stereotype.Service;

public interface EmailService {

    void sendCodeForLogin(String username, String code);
}
