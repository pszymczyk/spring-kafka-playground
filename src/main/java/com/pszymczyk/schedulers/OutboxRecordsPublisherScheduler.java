package com.pszymczyk.schedulers;

import com.pszymczyk.services.NaiveOutboxRecordsPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!kafkaConnect")
public class OutboxRecordsPublisherScheduler {

    private final NaiveOutboxRecordsPublisher naiveOutboxRecordsPublisher;

    public OutboxRecordsPublisherScheduler(NaiveOutboxRecordsPublisher naiveOutboxRecordsPublisher) {
        this.naiveOutboxRecordsPublisher = naiveOutboxRecordsPublisher;
    }

    @Scheduled(fixedDelay = 500)
    public void scheduleFixedDelayTask() {
        naiveOutboxRecordsPublisher.sendBatch();
    }
}
