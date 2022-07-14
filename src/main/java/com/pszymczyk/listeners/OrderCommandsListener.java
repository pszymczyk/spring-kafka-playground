package com.pszymczyk.listeners;

import com.pszymczyk.commands.AddItem;
import com.pszymczyk.commands.RemoveItem;
import com.pszymczyk.commands.UnknownCommand;
import com.pszymczyk.events.OrderEvent;
import com.pszymczyk.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Random;

@Component
//TODO move to configuration
@KafkaListener(topics = "order-commands")
@Transactional
public class OrderCommandsListener {

    private final Logger logger = LoggerFactory.getLogger(OrderCommandsListener.class);

    private final OrderService orderService;

    public OrderCommandsListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaHandler
    //TODO move application configuration
    @SendTo("orders")
    public OrderEvent listenOnOrderCommands(
            @Payload AddItem addItem,
            @Header(KafkaHeaders.OFFSET) long offset) {
        return orderService.handle(addItem, offset);
//        failSometimes();
    }

    @KafkaHandler
    //TODO move application configuration
    @SendTo("orders")
    public OrderEvent listenOnOrderCommands(
            @Payload RemoveItem removeItem,
            @Header(KafkaHeaders.OFFSET) long offset) {
        return orderService.handle(removeItem, offset);
//        failSometimes();
    }

    @KafkaHandler(isDefault = true)
    void listenDefault(@Payload UnknownCommand unknownCommand,
                       @Header(KafkaHeaders.OFFSET) long offset,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partitionId,
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

