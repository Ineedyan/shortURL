package com.example.shorturl.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shorturl.Entity.UrlMapping;
import com.example.shorturl.Mapper.UrlMappingMapper;
import com.example.shorturl.Service.ClickCountService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.shorturl.Utils.RedisConstants.CACHE_CLICK_COUNT_KEY;
import static com.example.shorturl.Utils.RedisConstants.CACHE_SHORT_URL;

@Service
public class ClickCountServiceImpl extends ServiceImpl<UrlMappingMapper, UrlMapping> implements ClickCountService {

    @Resource
    private UrlMappingMapper urlMappingMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 缓存更新点击量
     * @param shortUrl 短链接
     */
    @Override
    public void incrementClickCounts(String shortUrl){
        String redisKey = CACHE_CLICK_COUNT_KEY + shortUrl;
        stringRedisTemplate.opsForValue().increment(redisKey, 1);
    }

    /**
     * 获取点击量
     * @param shortUrl 短链接
     * @return 数量
     */
    @Override
    public Integer getClickCount(String shortUrl){
        String redisKey = CACHE_CLICK_COUNT_KEY + shortUrl;
        String result = stringRedisTemplate.opsForValue().get(redisKey);
        if(result == null){
            return 0;
        }
        return Integer.valueOf(result);
    }

    /**
     * 更新数据至数据库(每2小时执行一次)
     */
    @Scheduled(cron = "0 0 0/2 * * ?")
    @Transactional
    public void syncClickCountsToDatabase(){
        Set<String> keys = stringRedisTemplate.keys(CACHE_CLICK_COUNT_KEY + "*");
        if(keys == null || keys.isEmpty()){
            return;
        }
        List<UrlMapping> updateList = new ArrayList<>();
        for(String key: keys){
            String shortUrl = key.replace(CACHE_CLICK_COUNT_KEY, "");
            String countStr = stringRedisTemplate.opsForValue().get(key);
            if(countStr!=null){
                // 要更新的实体
                UrlMapping entity = new UrlMapping();
                entity.setClickCount(Integer.parseInt(countStr));
                entity.setShortUrl(shortUrl);
                entity.setUpdateTime(LocalDateTime.now());
                updateList.add(entity);
            }
        }
        if(!updateList.isEmpty()){
            // 更新数据库的值
            try {
                urlMappingMapper.updateBatchClickCount(updateList);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 更新数据库完成，清理redis中冗余数据，防止在更新数据库前程序崩溃，redis中数据丢失
        for (String key : keys) {
            stringRedisTemplate.delete(key);
        }
    }


}
