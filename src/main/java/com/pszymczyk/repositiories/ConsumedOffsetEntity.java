package com.pszymczyk.repositiories;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class ConsumedOffsetEntity {

    @Id
    @GeneratedValue
    private Long entityId;

    private Integer partition;

    private Long kafkaOffset;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public Long getKafkaOffset() {
        return kafkaOffset;
    }

    public void setKafkaOffset(Long offset) {
        this.kafkaOffset = offset;
    }

}
