package com.pszymczyk.listeners;

import com.pszymczyk.commands.OrderCommand;
import com.pszymczyk.services.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OrderCommandsListener {

    private final OrderService orderService;

    public OrderCommandsListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order-commands")
    public void listenOnOrderCommands(
        @Payload OrderCommand orderCommand,
        @Header(KafkaHeaders.OFFSET) long offset) {
        orderService.handle(orderCommand, offset);
        failSometimes();
    }

    private static void failSometimes() {
        Random rand = new Random();
        int randomNum = rand.nextInt((4 - 1) + 1) + 1;
        if (randomNum == 2) {
            System.err.println("BOOOM!!!!");
            System.exit(0);
        }
    }
}

