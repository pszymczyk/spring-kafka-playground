package com.pszymczyk.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicsConfiguration {
    @Bean
    NewTopic orderCommands() {
        return new NewTopic("order-commands", 1, (short) 1);
    }
}
