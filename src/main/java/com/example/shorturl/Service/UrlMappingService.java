package com.example.shorturl.Service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.shorturl.DTO.ShortenRequestDTO;
import com.example.shorturl.DTO.ShortenResponseDTO;
import com.example.shorturl.Entity.UrlMapping;
import com.example.shorturl.Utils.Result;


public interface UrlMappingService extends IService<UrlMapping>{
    /**
     * 短链接生成
     * @param shortenRequestDTO 长连接、所属分类
     * @return 响应
     */
    Result shortenUrl(ShortenRequestDTO shortenRequestDTO);

    String resolveUrl(String shortUrl);
}
