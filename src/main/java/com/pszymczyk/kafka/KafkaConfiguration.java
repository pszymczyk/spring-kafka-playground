package com.pszymczyk.kafka;

import com.pszymczyk.commands.OrderCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaOperations;

@Configuration
class KafkaConfiguration {

    @Bean
    ConcurrentMessageListenerContainerCustomizer concurrentMessageListenerContainerCustomizer(
            KafkaOperations<String, OrderCommand> kafkaOperations,
            ConcurrentKafkaListenerContainerFactory<String, OrderCommand> concurrentKafkaListenerContainerFactory) {
        var containerCustomizer = new ConcurrentMessageListenerContainerCustomizer(kafkaOperations);
        concurrentKafkaListenerContainerFactory.setContainerCustomizer(containerCustomizer);
        return containerCustomizer;
    }

}
