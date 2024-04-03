package com.atguigu.gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloSchedule {

    @Async
    @Scheduled(cron = "* * * ? * 7")
    public void hello() throws InterruptedException {
        log.info("Hello, Scheduling!");
        Thread.sleep(3000);
    }
}
