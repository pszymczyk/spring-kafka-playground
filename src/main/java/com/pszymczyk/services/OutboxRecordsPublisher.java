package com.pszymczyk.services;

import com.pszymczyk.repositiories.LastPublishedOutboxRecordEntity;
import com.pszymczyk.repositiories.LastPublishedOutboxRecordEntityRepository;
import com.pszymczyk.repositiories.OutboxRecordEntity;
import com.pszymczyk.repositiories.OutboxRepository;
import com.pszymczyk.topics.OrdersTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Naive implementation, on production use KafkaConnect
 */
@Service
public class OutboxRecordsPublisher {

    public static final long LAST_PUBLISHED_OUTBOX_RECORD_PLACEHOLDER = 999L;

    private final OutboxRepository outboxRepository;
    private final LastPublishedOutboxRecordEntityRepository lastPublishedOutboxRecordEntityRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OrdersTopic ordersTopic;

    public OutboxRecordsPublisher(OutboxRepository outboxRepository,
                                  LastPublishedOutboxRecordEntityRepository lastPublishedOutboxRecordEntityRepository,
                                  KafkaTemplate<String, String> kafkaTemplate,
                                  OrdersTopic ordersTopic) {
        this.outboxRepository = outboxRepository;
        this.lastPublishedOutboxRecordEntityRepository = lastPublishedOutboxRecordEntityRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.ordersTopic = ordersTopic;
    }

    @Transactional
    public void sendBatch() {
        LastPublishedOutboxRecordEntity lastPublishedRecord = lastPublishedOutboxRecordEntityRepository
            .findById(LAST_PUBLISHED_OUTBOX_RECORD_PLACEHOLDER)
            .orElseGet(() -> {
                LastPublishedOutboxRecordEntity lastPublishedOutboxRecordEntity = new LastPublishedOutboxRecordEntity();
                lastPublishedOutboxRecordEntity.setId(LAST_PUBLISHED_OUTBOX_RECORD_PLACEHOLDER);
                lastPublishedOutboxRecordEntity.setLastPublishedOutboxRecordId(0L);
                return lastPublishedOutboxRecordEntity;
            });

        Page<OutboxRecordEntity> page = outboxRepository.findByEntityIdGreaterThan(lastPublishedRecord.getLastPublishedOutboxRecordId(), PageRequest.of(0, 5));
        if (page.hasContent()) {
            page.getContent().forEach(outboxRecordEntity -> {
                kafkaTemplate.send(ordersTopic.getName(), outboxRecordEntity.getKey(), outboxRecordEntity.getJsonValue());
                lastPublishedRecord.setLastPublishedOutboxRecordId(outboxRecordEntity.getEntityId());
                lastPublishedOutboxRecordEntityRepository.save(lastPublishedRecord);
            });
        }
    }
}
