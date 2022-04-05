package com.pszymczyk.kafka;

import com.pszymczyk.commands.OrderCommand;
import org.springframework.kafka.config.ContainerCustomizer;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

public class ConcurrentMessageListenerContainerCustomizer implements ContainerCustomizer<String, OrderCommand, ConcurrentMessageListenerContainer<String, OrderCommand>> {

    private final KafkaOperations<String, OrderCommand> kafkaOperations;

    ConcurrentMessageListenerContainerCustomizer(KafkaOperations<String, OrderCommand> kafkaOperations) {
        this.kafkaOperations = kafkaOperations;
    }

    @Override
    public void configure(ConcurrentMessageListenerContainer<String, OrderCommand> container) {
        container.setErrorHandler(new SeekToCurrentErrorHandler(new DeadLetterPublishingRecoverer(kafkaOperations), new FixedBackOff(1000L, 5)));
    }
}
