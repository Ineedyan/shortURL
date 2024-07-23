package com.example.shorturl.DTO;

import lombok.Data;

@Data
public class WithShortUrlResponseDTO {
    // 生成的shortUrl
    private String shortUrl;

    public WithShortUrlResponseDTO(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
