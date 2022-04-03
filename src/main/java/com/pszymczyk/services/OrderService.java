package com.pszymczyk.services;

import com.pszymczyk.commands.OrderCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public void handle(OrderCommand orderCommand) {
        switch (orderCommand.getType()) {
            case "AddItem" -> logger.info("AddItem {}", orderCommand);
            case "RemoveItem" -> logger.info("RemoveItem {}", orderCommand);
            default -> throw new RuntimeException("Unknown command type " + orderCommand.getType());
        }
    }
}
