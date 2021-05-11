package com.pszymczyk.repositiories;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LastPublishedOutboxRecordEntity {
    @Id
    private Long id;

    private Long lastPublishedOutboxRecordId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLastPublishedOutboxRecordId() {
        return lastPublishedOutboxRecordId;
    }

    public void setLastPublishedOutboxRecordId(Long lastPublishedOutboxRecordId) {
        this.lastPublishedOutboxRecordId = lastPublishedOutboxRecordId;
    }
}
