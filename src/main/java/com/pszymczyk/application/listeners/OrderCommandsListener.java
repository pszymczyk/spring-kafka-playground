package com.pszymczyk.application.listeners;

import com.pszymczyk.application.commands.AddItem;
import com.pszymczyk.application.commands.RemoveItem;
import com.pszymczyk.application.commands.UnknownCommand;
import com.pszymczyk.application.events.OrderEvent;
import com.pszymczyk.application.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@KafkaListener(topics = "order-commands", containerFactory = "myKafkaContainerFactory")
@Component
//TODO move to configuration
public class OrderCommandsListener {

    private final Logger logger = LoggerFactory.getLogger(OrderCommandsListener.class);

    private final OrderService orderService;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderCommandsListener(OrderService orderService, KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaHandler
    @Transactional
    public void listenOnOrderCommands(
            @Payload AddItem addItem,
            @Header(KafkaHeaders.OFFSET) long offset) {
        orderService.handle(addItem, offset).forEach(event -> kafkaTemplate.send("orders", event));
//        failSometimes();
    }

    @KafkaHandler
    @Transactional
    public void listenOnOrderCommands(
            @Payload RemoveItem removeItem,
            @Header(KafkaHeaders.OFFSET) long offset) {
        orderService.handle(removeItem, offset).forEach(event -> kafkaTemplate.send("orders", event));
//        failSometimes();
    }

    @KafkaHandler(isDefault = true)
    void listenDefault(@Payload UnknownCommand unknownCommand,
                       @Header(KafkaHeaders.OFFSET) long offset,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        logger.warn("Handling unknown event type {}. Offset {}, partition {}, topic {}",
                unknownCommand.getType(),
                offset,
                partitionId,
                topic);
    }

    private static void failSometimes() {
        System.err.println("BOOOM!!!!");
        Random rand = new Random();
        int randomNum = rand.nextInt((4 - 1) + 1) + 1;
        if (randomNum == 2) {
            throw new RuntimeException();
        }
    }
}

