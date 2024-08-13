package com.example.shorturl.DTO;

import lombok.Data;

@Data
public class shortenRequestDTO {
    // 前缀 https || http
    private String prefixType;
    // 原链接
    private String longUrl;
    // 有效期
    private Integer days;
}
