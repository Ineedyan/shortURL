package com.example.shorturl.DTO;

import lombok.Data;

@Data
public class ShortenResponseDTO {
    // 生成的shortUrl
    private String shortUrl;

    public ShortenResponseDTO(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
