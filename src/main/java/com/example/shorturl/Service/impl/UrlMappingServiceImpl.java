package com.example.shorturl.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shorturl.DTO.WithLongUrlRequestDTO;
import com.example.shorturl.DTO.WithShortUrlResponseDTO;
import com.example.shorturl.Entity.UrlMapping;
import com.example.shorturl.Mapper.UrlMappingMapper;
import com.example.shorturl.Service.ClickCountService;
import com.example.shorturl.Service.UrlMappingService;
import com.example.shorturl.Utils.Result;
import com.example.shorturl.Utils.StrHashUtil;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.example.shorturl.Utils.RedisConstants.*;

@Service
public class UrlMappingServiceImpl extends ServiceImpl<UrlMappingMapper, UrlMapping> implements UrlMappingService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ClickCountService clickCountService;

    /**
     * 短链接生成
     * @param WithLongUrlRequestDTO 长链接、所属分类
     * @return 响应（短链接）
     */
    @Override
    public Result shortenUrl(WithLongUrlRequestDTO WithLongUrlRequestDTO) {
        // 获取请求参数
        String prefix = WithLongUrlRequestDTO.getPrefixType();
        String content = WithLongUrlRequestDTO.getLongUrl();
        // 拼接URL
        String url = prefix + "://" + content;
        // 拼接key
        String key1 = CACHE_LONG_URL + prefix + ":" + content;
        // 1.校验当前用于缩短的长链接是否已存在
        //  从Redis缓存中查询
        String urlJSON = stringRedisTemplate.opsForValue().get(key1);
        //  命中缓存，返回错误信息（已存在）
        if(StrUtil.isNotBlank(urlJSON)){
            return Result.fail("当前对应短链已存在！", new WithShortUrlResponseDTO(urlJSON));
        }
        //  未命中缓存，查询数据库
        UrlMapping urlMapping = query().eq("long_url", url).one();
        //  命中数据库，返回错误信息（已存在）
        if(urlMapping != null){
            stringRedisTemplate.opsForValue().set(key1, urlMapping.getShortUrl(), CACHE_URL_TTL, TimeUnit.DAYS);
            return Result.fail("当前对应短链已存在！", new WithShortUrlResponseDTO(urlMapping.getShortUrl()));
        }
        // 未命中，继续生成短链
        // 使用SHA-256 哈希算法 生成短链接
        String shortUrl = StrHashUtil.StrHash(url);
        // 保存到数据库中
        urlMapping = new UrlMapping();
        urlMapping.setLongUrl(url);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setCreateTime(LocalDateTime.now());
        urlMapping.setClickCount(0);
        save(urlMapping);
        // 保存到Redis中，并设置有效期为30天
        stringRedisTemplate.opsForValue().set(key1, urlMapping.getShortUrl(), CACHE_URL_TTL, TimeUnit.DAYS);
        String key2 = CACHE_SHORT_URL + shortUrl;
        stringRedisTemplate.opsForValue().set(key2, urlMapping.getLongUrl(), CACHE_URL_TTL, TimeUnit.DAYS);
        // 创建返回对象
        WithShortUrlResponseDTO WithShortUrlResponseDTO = BeanUtil.copyProperties(urlMapping, WithShortUrlResponseDTO.class);
        return Result.ok(WithShortUrlResponseDTO);
    }

    @Override
    public String resolveUrl(String shortUrl) {
        // 解析短链接
        // 根据shortUrl从Redis中查询longUrl
        String key = CACHE_SHORT_URL + shortUrl;
        String longUrl = stringRedisTemplate.opsForValue().get(key);
        // 命中，返回
        if(StrUtil.isNotBlank(longUrl)){
            clickCountService.incrementClickCounts(shortUrl);
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
        // 命中，缓存到Redis中，并设置有效期为30天
        String[] spilt = urlMapping.getLongUrl().split("://");
        String key1 = CACHE_LONG_URL + spilt[0] + ":" + spilt[1];
        stringRedisTemplate.opsForValue().set(key1, urlMapping.getShortUrl(), CACHE_URL_TTL, TimeUnit.DAYS);
        String key2 = CACHE_SHORT_URL + shortUrl;
        stringRedisTemplate.opsForValue().set(key2, urlMapping.getLongUrl(), CACHE_URL_TTL, TimeUnit.DAYS);
        // 计数
        clickCountService.incrementClickCounts(shortUrl);
        // 返回前端
        return urlMapping.getLongUrl();
    }



}
