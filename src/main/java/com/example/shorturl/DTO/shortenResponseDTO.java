package com.example.shorturl.DTO;

import lombok.Data;

@Data
public class shortenResponseDTO {
    // 生成的shortUrl
    private String shortUrl;

    public shortenResponseDTO(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
