package com.example.shorturl.Service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.shorturl.DTO.WithLongUrlRequestDTO;
import com.example.shorturl.Entity.UrlMapping;
import com.example.shorturl.Utils.Result;


public interface UrlMappingService extends IService<UrlMapping>{
    /**
     * 短链接生成
     * @param WithLongUrlRequestDTO 长连接、所属分类
     * @return 响应
     */
    Result shortenUrl(WithLongUrlRequestDTO WithLongUrlRequestDTO);

    /**
     * 解析短链接
     * @param shortUrl 短链
     * @return 解析结果
     */
    String resolveUrl(String shortUrl);


}
