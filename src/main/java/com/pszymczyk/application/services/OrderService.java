package com.pszymczyk.application.services;

import com.pszymczyk.application.commands.AddItem;
import com.pszymczyk.application.commands.RemoveItem;
import com.pszymczyk.application.events.ItemAdded;
import com.pszymczyk.application.events.ItemRemoved;
import com.pszymczyk.application.events.OrderEvent;
import com.pszymczyk.application.repositiories.OrderEntity;
import com.pszymczyk.application.repositiories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.singletonList;

@Service
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderEvent> handle(RemoveItem removeItem, long offset) {
        OrderEntity orderEntity = getOrderEntity(removeItem.getOrderId());

        if (orderEntity.getLastAppliedOffset() != null && orderEntity.getLastAppliedOffset() >= offset) {
            logger.info("Skipping duplicate message with offset " + offset);
        }

        orderEntity.setLastAppliedOffset(offset);
        orderRepository.save(orderEntity);
        return singletonList(new ItemRemoved(removeItem.getOrderId(), removeItem.getItem()));
    }

    public List<OrderEvent> handle(AddItem addItem, long offset) {
        OrderEntity orderEntity = getOrderEntity(addItem.getOrderId());

        if (orderEntity.getLastAppliedOffset() != null && orderEntity.getLastAppliedOffset() >= offset) {
            logger.info("Skipping duplicate message with offset " + offset);
        }

        orderEntity.setLastAppliedOffset(offset);
        orderRepository.save(orderEntity);

        return singletonList(new ItemAdded(addItem.getOrderId(), addItem.getItem()));
    }

    private OrderEntity getOrderEntity(String orderId) {
        return orderRepository.findByOrderId(orderId).orElse(new OrderEntity(orderId));
    }
}
