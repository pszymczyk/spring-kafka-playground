package com.pszymczyk.listeners;

import com.pszymczyk.commands.OrderCommand;
import com.pszymczyk.services.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrderCommandsListener {

    private final OrderService orderService;

    public OrderCommandsListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order-commands",
        groupId = "spring-kafka-playground",
        containerFactory = "ordersListenerContainer")
    public void listenOnOrderCommands(
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
        @Header(KafkaHeaders.OFFSET) long offset,
        @Payload OrderCommand orderCommand,
        Acknowledgment acknowledgment) {
        orderService.handle(orderCommand, partition, offset);
        acknowledgment.acknowledge();
    }



}
