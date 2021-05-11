package com.pszymczyk.repositiories;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class ConsumedOffsetEntity {

    @Id
    @Column(unique = true)
    private Integer partition;

    private Long kafkaOffset;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConsumedOffsetEntity that = (ConsumedOffsetEntity) o;
        return Objects.equals(partition, that.partition) && Objects.equals(kafkaOffset, that.kafkaOffset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partition, kafkaOffset);
    }
}
