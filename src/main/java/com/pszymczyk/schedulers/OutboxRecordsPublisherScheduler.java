package com.pszymczyk.schedulers;

import com.pszymczyk.services.NativeOutboxRecordsPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!kafkaConnect")
public class OutboxRecordsPublisherScheduler {

    private final NativeOutboxRecordsPublisher nativeOutboxRecordsPublisher;

    public OutboxRecordsPublisherScheduler(NativeOutboxRecordsPublisher nativeOutboxRecordsPublisher) {
        this.nativeOutboxRecordsPublisher = nativeOutboxRecordsPublisher;
    }

    @Scheduled(fixedDelay = 500)
    public void scheduleFixedDelayTask() {
        nativeOutboxRecordsPublisher.sendBatch();
    }
}
