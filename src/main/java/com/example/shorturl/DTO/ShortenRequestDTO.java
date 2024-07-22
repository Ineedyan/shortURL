package com.example.shorturl.DTO;

import lombok.Data;

@Data
public class ShortenRequestDTO {
    // 原始URL
    private String longUrl;
}
