package com.example.shorturl.Service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.shorturl.DTO.shortenRequestDTO;
import com.example.shorturl.Entity.UrlMapping;
import com.example.shorturl.Utils.Result;

import java.time.LocalDateTime;


public interface UrlMappingService extends IService<UrlMapping>{
    /**
     * 短链接生成
     * @param shortenRequestDTO 长连接、所属分类、有效期
     * @return 响应
     */
    Result shortenUrl(shortenRequestDTO shortenRequestDTO);

    /**
     * 解析短链接
     * @param shortUrl 短链
     * @return 解析结果
     */
    String resolveUrl(String shortUrl);

    /**
     * 删除过期短链数据
     * @param now 删除数据
     * @return 删除记录数
     */
    int deleteExpiredData(LocalDateTime now);
}
