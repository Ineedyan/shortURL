package com.example.shorturl.Job;

import com.example.shorturl.Service.ClickCountService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;

@Slf4j
public class SyncClickCountJob extends QuartzJobBean {

    @Resource
    private ClickCountService clickCountService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            clickCountService.syncClickCountsToDatabase();
            log.info("{}：刷新t_mapping短链接点击数据", LocalDateTime.now());
        } catch (Exception e) {
            log.info("{}：刷新t_mapping短链接点击数据出错", LocalDateTime.now());
            throw new RuntimeException(e);
        }
    }
}
