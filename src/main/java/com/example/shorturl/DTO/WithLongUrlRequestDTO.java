package com.example.shorturl.DTO;

import lombok.Data;

@Data
public class WithLongUrlRequestDTO {
    // 前缀 https || http
    private String prefixType;
    // 原链接
    private String longUrl;
}
