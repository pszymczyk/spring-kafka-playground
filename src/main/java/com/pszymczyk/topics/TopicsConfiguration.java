package com.pszymczyk.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicsConfiguration {

    @Bean
    NewTopic orders(OrderCommandsTopic orderCommandsTopic) {
        return new NewTopic(orderCommandsTopic.getName(), 1, (short) 1);
    }

    @Bean
    NewTopic ordersDLT(OrderCommandsTopic orderCommandsTopic) {
        return new NewTopic(orderCommandsTopic.getName()+".DLT", 1, (short) 1);
    }
}
