package com.pszymczyk.services;

import com.pszymczyk.commands.AddItem;
import com.pszymczyk.commands.RemoveItem;
import com.pszymczyk.events.ItemAdded;
import com.pszymczyk.events.OrderEvent;
import com.pszymczyk.repositiories.OrderEntity;
import com.pszymczyk.repositiories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderEvent handle(RemoveItem removeItem, long offset) {
        OrderEntity orderEntity = getOrderEntity(removeItem.getOrderId());

        if (orderEntity.getLastAppliedOffset() != null && orderEntity.getLastAppliedOffset() >= offset) {
            logger.info("Skipping duplicate message with offset " + offset);
        }

        orderEntity.setLastAppliedOffset(offset);
        orderRepository.save(orderEntity);
        return new ItemAdded(removeItem.getOrderId(), removeItem.getItem());
    }

    public OrderEvent handle(AddItem addItem, long offset) {
        OrderEntity orderEntity = getOrderEntity(addItem.getOrderId());

        if (orderEntity.getLastAppliedOffset() != null && orderEntity.getLastAppliedOffset() >= offset) {
            logger.info("Skipping duplicate message with offset " + offset);
        }

        orderEntity.setLastAppliedOffset(offset);
        orderRepository.save(orderEntity);

        return new ItemAdded(addItem.getOrderId(), addItem.getItem());
    }

    private OrderEntity getOrderEntity(String orderId) {
        return orderRepository.findByOrderId(orderId).orElse(new OrderEntity(orderId));
    }
}
