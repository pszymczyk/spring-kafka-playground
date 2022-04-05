package com.pszymczyk.services;

import com.pszymczyk.commands.OrderCommand;
import com.pszymczyk.repositiories.OrderEntity;
import com.pszymczyk.repositiories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void handle(OrderCommand orderCommand) {
        OrderEntity orderEntity = getOrderEntity(orderCommand);

        switch (orderCommand.getType()) {
            case "AddItem" -> orderEntity.addItem(orderCommand.getItem());
            case "RemoveItem" -> orderEntity.removeItem(orderCommand.getItem());
            default -> throw new RuntimeException("Unknown command type " + orderCommand.getType());
        }

        orderRepository.save(orderEntity);
    }

    private OrderEntity getOrderEntity(OrderCommand orderCommand) {
        return orderRepository.findByOrderId(orderCommand.getOrderId()).orElse(new OrderEntity(orderCommand.getOrderId()));
    }
}
