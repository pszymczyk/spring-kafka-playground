package com.pszymczyk.schedulers;

import com.pszymczyk.services.OutboxRecordsPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!kafkaConnect")
public class OutboxRecordsPublisherScheduler {

    private final OutboxRecordsPublisher outboxRecordsPublisher;

    public OutboxRecordsPublisherScheduler(OutboxRecordsPublisher outboxRecordsPublisher) {
        this.outboxRecordsPublisher = outboxRecordsPublisher;
    }

    @Scheduled(fixedDelay = 50)
    public void scheduleFixedDelayTask() {
        outboxRecordsPublisher.sendBatch();
    }
}
