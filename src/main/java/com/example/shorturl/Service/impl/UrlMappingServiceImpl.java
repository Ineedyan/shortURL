package com.example.shorturl.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
import java.util.concurrent.TimeUnit;

import static com.example.shorturl.Utils.RedisConstants.*;

@Service
public class UrlMappingServiceImpl extends ServiceImpl<UrlMappingMapper, UrlMapping> implements UrlMappingService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 短链接生成
     * @param shortenRequestDTO 长链接、所属分类
     * @return 响应（短链接）
     */
    @Override
    public Result shortenUrl(ShortenRequestDTO shortenRequestDTO) {
        String key1 = CACHE_LONG_URL + shortenRequestDTO.getLongUrl();
        // 1.校验当前用于缩短的长链接是否已存在
        //  从Redis缓存中查询
        String urlJSON = stringRedisTemplate.opsForValue().get(key1);
        //  命中缓存，返回错误信息（已存在）
        if(StrUtil.isNotBlank(urlJSON)){
            return Result.fail("当前对应短链已存在！", new ShortenResponseDTO(urlJSON));
        }
        //  未命中缓存，查询数据库
        UrlMapping urlMapping = query().eq("long_url", shortenRequestDTO.getLongUrl()).one();
        //  命中数据库，返回错误信息（已存在）
        if(urlMapping != null){
            return Result.fail("当前对应短链已存在！", new ShortenResponseDTO(urlMapping.getShortUrl()));
        }
        // 未命中，继续生成短链
        // 获取原始链接（长链接）
        String longUrl = shortenRequestDTO.getLongUrl();
        // 使用SHA-256 哈希算法 生成短链接
        String shortUrl = StrHashUtil.StrHash(longUrl);
        // 保存到数据库中
        urlMapping = new UrlMapping();
        urlMapping.setLongUrl(longUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setCreateTime(LocalDateTime.now());
        save(urlMapping);
        // 保存到Redis中，并设置有效期为30天
        stringRedisTemplate.opsForValue().set(key1, urlMapping.getShortUrl(), CACHE_URL_TTL, TimeUnit.DAYS);
        String key2 = CACHE_SHORT_URL + shortUrl;
        stringRedisTemplate.opsForValue().set(key2, urlMapping.getLongUrl(), CACHE_URL_TTL, TimeUnit.DAYS);
        // 创建返回对象
        ShortenResponseDTO shortenResponseDTO = BeanUtil.copyProperties(urlMapping, ShortenResponseDTO.class);
        return Result.ok(shortenResponseDTO);
    }

    @Override
    public String resolveUrl(String shortUrl) {
        // 解析短链接
        // 根据shortUrl从Redis中查询longUrl
        String key = CACHE_SHORT_URL + shortUrl;
        String longUrl = stringRedisTemplate.opsForValue().get(key);
        // 命中，返回
        if(StrUtil.isNotBlank(longUrl)){
            return longUrl;
        }
        // 命中但为空
        if(longUrl != null){
            // 防止缓存穿透，返回空值
            return null;
        }
        // 未命中，查询数据库
        UrlMapping urlMapping = query().eq("short_url", shortUrl).one();
        // 未命中，返回空值，并给Redis赋值为“”
        if(urlMapping ==  null){
            stringRedisTemplate.opsForValue().set(key, "", CACHE_URL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 命中，返回longUrl
        return urlMapping.getLongUrl();
    }

}
