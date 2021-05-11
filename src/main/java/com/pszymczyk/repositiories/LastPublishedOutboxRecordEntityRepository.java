package com.pszymczyk.repositiories;

import org.springframework.data.repository.CrudRepository;

public interface LastPublishedOutboxRecordEntityRepository extends CrudRepository<LastPublishedOutboxRecordEntity, Long> {
}
