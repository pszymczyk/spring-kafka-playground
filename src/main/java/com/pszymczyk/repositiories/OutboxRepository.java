package com.pszymczyk.repositiories;

import org.springframework.data.repository.CrudRepository;

public interface OutboxRepository extends CrudRepository<OutboxRecord, Long> {
}
