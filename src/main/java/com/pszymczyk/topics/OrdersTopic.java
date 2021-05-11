package com.pszymczyk.topics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrdersTopic {

    private final String name;

    public OrdersTopic(@Value("${playground.kafka.topics.orders}") String topic) {
        this.name = topic;
    }

    public String getName() {
        return name;
    }
}
