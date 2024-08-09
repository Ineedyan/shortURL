package com.example.shorturl.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.shorturl.Entity.UrlMapping;

public interface ClickCountService extends IService<UrlMapping> {


    void incrementClickCounts(String shortUrl);

    Integer getClickCount(String shortUrl);

    void syncClickCountsToDatabase();
}
