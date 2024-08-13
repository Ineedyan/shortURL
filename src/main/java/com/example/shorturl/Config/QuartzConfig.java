package com.example.shorturl.Config;

import com.example.shorturl.Job.ExpireDataCleanupJob;
import com.example.shorturl.Job.SyncClickCountJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;;

@Configuration
public class QuartzConfig {
    /**
     * 清除过期短链数据 定时任务
     * 执行频率： 每日0：00
     */
    @Bean
    public JobDetail expireDataCleanupDetail(){
        return JobBuilder.newJob(ExpireDataCleanupJob.class)
                .withIdentity("expireDataCleanupJob")
                .storeDurably()
                .build();
    }
    @Bean
    public Trigger expireDataCleanupTrigger(){
        return TriggerBuilder.newTrigger()
                .forJob(expireDataCleanupDetail())
                .withIdentity("expireDataCleanupTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
                .build();
    }

    /**
     * 刷新点击数据至数据库
     * 执行频率： 每两小时刷新一次
     */
    @Bean
    public JobDetail syncClickCountDataDetail(){
        return JobBuilder.newJob(SyncClickCountJob.class)
                .withIdentity("syncClickCountDataJob")
                .storeDurably()
                .build();
    }
    @Bean
    public Trigger syncClickCountDataTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(syncClickCountDataDetail())
                .withIdentity("syncClickCountDataTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0/2 * * ?"))
                .build();
    }


}
