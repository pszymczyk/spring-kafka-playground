package com.pszymczyk.repositiories;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByOrderId(String orderId);
}
