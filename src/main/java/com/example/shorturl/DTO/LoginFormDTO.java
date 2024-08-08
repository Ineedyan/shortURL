package com.example.shorturl.DTO;


import lombok.Data;

@Data
public class LoginFormDTO {

    private Byte loginType;
    private String username;
    private String code;
    private String password;
}
