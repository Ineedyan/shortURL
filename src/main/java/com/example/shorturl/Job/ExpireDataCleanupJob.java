package com.example.shorturl.Job;

import com.example.shorturl.Service.UrlMappingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ExpireDataCleanupJob extends QuartzJobBean {

    @Resource
    private UrlMappingService urlMappingService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LocalDateTime now = LocalDateTime.now();
        try {
            int deleteCount = urlMappingService.deleteExpiredData(now);
            log.info("{}：清理t_mapping过期数据. 删除{}条记录", now, deleteCount);
        } catch (Exception e) {
            log.info("{}：清理过期数据出错： {}", now, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
