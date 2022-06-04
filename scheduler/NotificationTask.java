package com.sploot.api.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationTask {
    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        log.error(
                "Fixed delay task - " + System.currentTimeMillis() / 1000);
    }
}
