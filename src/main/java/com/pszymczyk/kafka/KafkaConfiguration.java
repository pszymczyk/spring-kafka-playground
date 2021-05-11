package com.pszymczyk.kafka;

import com.pszymczyk.commands.OrderCommand;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCommand> ordersListenerContainer(KafkaProperties kafkaProperties, KafkaOperations<Object, Object> template) {
        ConcurrentKafkaListenerContainerFactory<String, OrderCommand> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaProperties));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setErrorHandler(new SeekToCurrentErrorHandler(new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 2)));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderCommand> consumerFactory(KafkaProperties properties) {
        Map<String, Object> props = properties.buildConsumerProperties();
        DefaultKafkaConsumerFactory<String, OrderCommand> consumerFactory = new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new JsonDeserializer<>(OrderCommand.class).ignoreTypeHeaders());

        return consumerFactory;
    }

    /**
     * Second way of register custom deserializer - https://docs.spring.io/spring-kafka/reference/html/#messaging-message-conversion
     *
     * "Although the Serializer and Deserializer API is quite simple and flexible from the low-level Kafka Consumer and Producer perspective,
     * you might need more flexibility at the Spring Messaging level, when using either @KafkaListener or Spring Integration’s Apache Kafka Support.
     * To let you easily convert to and from org.springframework.messaging.Message, Spring for Apache Kafka provides a MessageConverter abstraction
     * with the MessagingMessageConverter implementation and its JsonMessageConverter (and subclasses) customization.
     * You can inject the MessageConverter into a KafkaTemplate instance directly and by using AbstractKafkaListenerContainerFactory bean definition
     * for the @KafkaListener.containerFactory() property."
     *
     */

//    @Bean
    public ConcurrentKafkaListenerContainerFactory<byte[], byte[]> ordersListenerContainer2(KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<byte[], byte[]> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory2(kafkaProperties));
        factory.setMessageConverter(new JsonMessageConverter());
        return factory;
    }

//    @Bean
    public ConsumerFactory<byte[], byte[]> consumerFactory2(KafkaProperties properties) {
        Map<String, Object> props = properties.buildConsumerProperties();
        DefaultKafkaConsumerFactory<byte[], byte[]> consumerFactory = new DefaultKafkaConsumerFactory<>(
            props,
            new ByteArrayDeserializer(),
            new ByteArrayDeserializer());

        return consumerFactory;
    }

}
