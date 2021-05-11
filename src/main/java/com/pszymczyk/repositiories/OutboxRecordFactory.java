package com.pszymczyk.repositiories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class OutboxRecordFactory {

    private final ObjectMapper objectMapper;

    public OutboxRecordFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OutboxRecordEntity create(String key, Object value) {
        OutboxRecordEntity outboxRecordEntity = new OutboxRecordEntity();
        outboxRecordEntity.setKey(key);
        outboxRecordEntity.setJsonValue(serialize(value));
        return outboxRecordEntity;
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize outbox record value! " + value);
        }
    }
}
