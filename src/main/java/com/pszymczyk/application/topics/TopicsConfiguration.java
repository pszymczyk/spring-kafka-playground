package com.pszymczyk.application.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicsConfiguration {
    @Bean
    NewTopic orderCommands() {
        return new NewTopic("order-commands", 1, (short) 1);
    }

    @Bean
    NewTopic orderCommandsDLT() {
        return new NewTopic("order-commands.DLT", 1, (short) 1);
    }
}
