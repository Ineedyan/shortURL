package com.example.shorturl.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shorturl.DTO.shortenRequestDTO;
import com.example.shorturl.DTO.shortenResponseDTO;
import com.example.shorturl.Entity.UrlMapping;
import com.example.shorturl.Mapper.UrlMappingMapper;
import com.example.shorturl.Service.ClickCountService;
import com.example.shorturl.Service.UrlMappingService;
import com.example.shorturl.Utils.Result;
import com.example.shorturl.Utils.StrHashUtil;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.example.shorturl.Utils.RedisConstants.*;

@Service
public class UrlMappingServiceImpl extends ServiceImpl<UrlMappingMapper, UrlMapping> implements UrlMappingService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ClickCountService clickCountService;

    @Resource
    private UrlMappingMapper urlMappingMapper;

    // 时区
    private final String timeZones = "+8";

    /**
     * 短链接生成
     * @param shortenRequestDTO 长链接、所属分类、有效期
     * @return 响应（短链接）
     */
    @Override
    public Result shortenUrl(shortenRequestDTO shortenRequestDTO) {
        // 获取请求参数
        String prefix = shortenRequestDTO.getPrefixType();
        String content = shortenRequestDTO.getLongUrl();
        // 拼接URL
        String url = prefix + "://" + content;

        // 拼接key
        String key1 = CACHE_LONG_URL + prefix + ":" + content;

        // 校验当前用于缩短的长链接是否已存在
        //  从Redis缓存中查询
        String urlJSON = stringRedisTemplate.opsForValue().get(key1);
        //  命中缓存，返回错误信息（已存在）
        if(StrUtil.isNotBlank(urlJSON)){
            return Result.fail("当前链接对应短链已存在！", new shortenResponseDTO(urlJSON));
        }
        //  未命中缓存，查询数据库
        UrlMapping urlMapping = query().eq("long_url", url).one();
        /*
            数据库存在对应记录，两种情况：
            1. 短链实际已经过期，但还没被数据库删除
            2. 短链未过期
        */
        if(urlMapping != null){
            // 获取实际过期时间与当前时间的时间差
            LocalDateTime realExpireTime = urlMapping.getExpireTime();
            long second = realExpireTime.toEpochSecond(ZoneOffset.of(timeZones)) -
                    LocalDateTime.now().toEpochSecond(ZoneOffset.of(timeZones));
            // 未过期，读入Redis，做防止缓存雪崩措施
            if(second > 0){
                // 获取0.2 - 0.5之间的随机浮点数
                double randomFactor = ThreadLocalRandom.current().nextDouble(0.2, 0.5);
                long redisExpireTime = (long) (second * randomFactor);
                // 存入Redis
                stringRedisTemplate.opsForValue().set(key1, urlMapping.getShortUrl(), redisExpireTime, TimeUnit.SECONDS);
                return Result.fail("当前链接对应短链已存在！", new shortenResponseDTO(urlMapping.getShortUrl()));
            }
            // 已过期，删除数据库对应记录，继续短链生成逻辑
            QueryWrapper<UrlMapping> wrapper = new QueryWrapper<>();
            wrapper.eq("short_url", urlMapping.getShortUrl());
            remove(wrapper);
        }

        // 未命中，继续生成短链
        // 使用SHA-256 哈希算法 生成短链接
        String shortUrl = StrHashUtil.StrHash(url);
        // 对短链判重
        String key2 = CACHE_SHORT_URL + shortUrl;
        if(StrUtil.isNotBlank(stringRedisTemplate.opsForValue().get(key2)) || query().eq("short_url", shortUrl).one() != null){
            String newUrl = url + "[REPEAT]";
            shortUrl = StrHashUtil.StrHash(newUrl);
        }

        // 保存到数据库中
        urlMapping = new UrlMapping();
        urlMapping.setLongUrl(url);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setExpireTime(LocalDateTime.now().plusDays(shortenRequestDTO.getDays()));
        urlMapping.setCreateTime(LocalDateTime.now());
        urlMapping.setClickCount(0);
        save(urlMapping);

        // 保存到Redis中，对过期时间进行处理
        double randomFactor = ThreadLocalRandom.current().nextDouble(0.2, 0.5);
        long diff = urlMapping.getExpireTime().toEpochSecond(ZoneOffset.of(timeZones)) -
                LocalDateTime.now().toEpochSecond(ZoneOffset.of(timeZones));
        long redisExpireTime = (long) (diff * randomFactor);
        stringRedisTemplate.opsForValue().set(key1, urlMapping.getShortUrl(), redisExpireTime, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(key2, urlMapping.getLongUrl(), redisExpireTime, TimeUnit.SECONDS);

        // 创建返回对象
        shortenResponseDTO shortenResponseDTO = BeanUtil.copyProperties(urlMapping, shortenResponseDTO.class);
        return Result.ok(shortenResponseDTO);
    }


    /**
     * 解析短链接
     * @param shortUrl 短链
     * @return 解析结果
     */
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


    /**
     * 删除过期短链数据
     * @param now 删除数据
     * @return 删除记录数
     */
    @Override
    @Transactional
    public int deleteExpiredData(LocalDateTime now) {
        return urlMappingMapper.deleteExpiredData(now);
    }


}
