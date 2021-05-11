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

    public OutboxRecord create(String key, Object value) {
        OutboxRecord outboxRecord = new OutboxRecord();
        outboxRecord.setKey(key);
        outboxRecord.setJsonValue(serialize(value));
        return outboxRecord;
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize outbox record value! " + value);
        }
    }
}
