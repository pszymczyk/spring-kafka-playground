package com.pszymczyk.repositiories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OutboxRepository extends PagingAndSortingRepository<OutboxRecordEntity, Long> {
    Page<OutboxRecordEntity> findByEntityIdGreaterThan(Long lastPublished, Pageable pageable);
}
