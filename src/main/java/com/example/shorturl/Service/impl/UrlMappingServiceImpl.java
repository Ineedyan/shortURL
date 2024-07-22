package com.example.shorturl.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shorturl.DTO.ShortenRequestDTO;
import com.example.shorturl.DTO.ShortenResponseDTO;
import com.example.shorturl.Entity.UrlMapping;
import com.example.shorturl.Mapper.UrlMappingMapper;
import com.example.shorturl.Service.UrlMappingService;
import com.example.shorturl.Utils.Result;
import com.example.shorturl.Utils.StrHashUtil;

import org.apache.ibatis.io.ResolverUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class UrlMappingServiceImpl extends ServiceImpl<UrlMappingMapper, UrlMapping> implements UrlMappingService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 短链接生成
     * @param shortenRequestDTO 长连接、所属分类
     * @return 响应（短链接）
     */
    @Override
    public Result shortenUrl(ShortenRequestDTO shortenRequestDTO) {
        // 获取原始链接（长链接）
        String longUrl = shortenRequestDTO.getLongUrl();
        String shortUrl = StrHashUtil.StrHash(longUrl);
        // 保存到数据库中
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setLongUrl(longUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setCreateTime(LocalDateTime.now());
        save(urlMapping);
        // 创建返回对象
        ShortenResponseDTO shortenResponseDTO = BeanUtil.copyProperties(urlMapping, ShortenResponseDTO.class);
        return Result.ok(shortenResponseDTO);
    }

    @Override
    public String resolveUrl(String shortUrl) {
        // 解析短链接
        // 根据shortUrl从数据库中查询longUrl
        UrlMapping urlMapping = query().eq("short_url", shortUrl).one();
        // 重定向到longUrl
        return urlMapping.getLongUrl();
    }

}
