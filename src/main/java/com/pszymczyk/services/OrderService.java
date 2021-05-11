package com.pszymczyk.services;

import com.pszymczyk.commands.OrderCommand;
import com.pszymczyk.events.ItemAdded;
import com.pszymczyk.events.ItemRemoved;
import com.pszymczyk.repositiories.ConsumedOffsetEntity;
import com.pszymczyk.repositiories.OrderEntity;
import com.pszymczyk.repositiories.OrderRepository;
import com.pszymczyk.repositiories.OutboxRecordEntity;
import com.pszymczyk.repositiories.OutboxRecordFactory;
import com.pszymczyk.repositiories.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final OutboxRecordFactory outboxRecordFactory;

    public OrderService(OrderRepository orderRepository, OutboxRepository outboxRepository, OutboxRecordFactory outboxRecordFactory) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.outboxRecordFactory = outboxRecordFactory;
    }

    @Transactional
    public void handle(OrderCommand orderCommand, int partition, long offset) {
        OrderEntity orderEntity = orderRepository.findByOrderId(orderCommand.getOrderId()).orElse(new OrderEntity());

        if (repeatedCommand(orderEntity, partition, offset)) {
            logger.warn("Skipping duplicate message, topic {}, partition, {}, offset {}.", "order-commands", partition, offset);
            return;
        }

        switch (orderCommand.getType()) {
            case "AddItem" -> addItem(orderEntity, orderCommand, partition, offset);
            case "RemoveItem" -> removeItem(orderEntity, orderCommand, partition, offset);
            default -> throw new RuntimeException("Unknown command type " + orderCommand.getType());
        }
    }

    private boolean repeatedCommand(OrderEntity orderEntity, int partition, long offset) {
        if (orderEntity.getConsumedOffsets() == null) {
            return false;
        }

        Long lastConsumedOffsetForPartition = orderEntity.getConsumedOffsets()
            .stream()
            .filter(consumedOffsetEntity1 -> consumedOffsetEntity1.getPartition().equals(partition))
            .map(ConsumedOffsetEntity::getKafkaOffset)
            .findAny()
            .orElse(-1L);

        return lastConsumedOffsetForPartition > offset;
    }

    private void addItem(OrderEntity orderEntity, OrderCommand orderCommand, int partition, long offset) {
        if (orderEntity.getOrderId() == null) {
            orderEntity.addItem(orderCommand.getOrderId(), orderCommand.getItem(), partition, offset);
        } else {
            orderEntity.addItem(orderCommand.getItem(), partition, offset);
        }
        orderRepository.save(orderEntity);

        OutboxRecordEntity outboxRecordEntity = outboxRecordFactory.create(orderEntity.getOrderId(),
            new ItemAdded(orderCommand.getOrderId(), orderCommand.getItem()));
        outboxRepository.save(outboxRecordEntity);
    }

    private void removeItem(OrderEntity orderEntity, OrderCommand orderCommand, int partition, long offset) {
        if (orderEntity.getOrderId() == null) {
            throw new RuntimeException("Cannot remove item from order: " + orderCommand.getOrderId());
        } else {
            orderEntity.removeItem(orderCommand.getItem(), partition, offset);
        }

        orderRepository.save(orderEntity);

        OutboxRecordEntity outboxRecordEntity = outboxRecordFactory.create(orderEntity.getOrderId(),
            new ItemRemoved(orderCommand.getOrderId(), orderCommand.getItem()));
        outboxRepository.save(outboxRecordEntity);
    }
}
