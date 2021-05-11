package com.pszymczyk.controllers;

import com.fasterxml.jackson.annotation.JsonValue;

public class CommandId {

    private final String topic;
    private final int partition;
    private final long offset;

    public CommandId(String topic, int partition, long offset) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
    }

    @JsonValue
    String commandIdAsString() {
        return String.format("%s.%d.%d", topic, partition, offset);
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }
}
