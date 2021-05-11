package com.pszymczyk.topics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderCommandsTopic {

    private final String name;

    public OrderCommandsTopic(@Value("${playground.kafka.topics.orderCommands}") String topic) {
        this.name = topic;
    }

    public String getName() {
        return name;
    }
}
