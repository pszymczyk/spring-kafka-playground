package com.pszymczyk.listeners;

import com.pszymczyk.commands.OrderCommand;
import com.pszymczyk.services.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrderCommandsListener {

    private final OrderService orderService;

    public OrderCommandsListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order-commands")
    public void listenOnOrderCommands(
        @Payload OrderCommand orderCommand) {
        orderService.handle(orderCommand);
    }
}
