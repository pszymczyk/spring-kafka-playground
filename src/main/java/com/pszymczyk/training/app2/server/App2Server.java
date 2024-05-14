package com.pszymczyk.training.app2.server;

import com.pszymczyk.training.app2.client.App2Client;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@SpringBootApplication
public class App2Server {

    private final Logger logger = LoggerFactory.getLogger(App2Server.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App2Server.class);
        application.setDefaultProperties(Map.of("server.port", "8082"));
        application.run(args);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> myKafkaContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, "app2",
                JsonDeserializer.TRUSTED_PACKAGES, "com.pszymczyk.training.app2.*",
                JsonDeserializer.VALUE_DEFAULT_TYPE, Object.class));
    }

    @Bean
    public NewTopic messages() {
        return TopicBuilder.name(App2Client.APP_2_MESSAGES_AND_REQUESTS)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Component
    @KafkaListener(topics = "app2-messages-and-requests", containerFactory = "myKafkaContainerFactory")
    public class MyKafkaHandler {

        @KafkaHandler
        void handleMessages(Message message) {
            logger.info("Server received message {}", message);
        }

        @KafkaHandler
        void handleRequests(Request request) {
            logger.info("Server received request {}", request);
        }

        @KafkaHandler(isDefault = true)
        void handleRequests(@Payload Object unknown,
                            @Header(KafkaHeaders.OFFSET) long offset,
                            @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
            logger.info("Server received unknown message {},{},{}", offset, partitionId, topic);
            logger.info("Unknown message to string {}", unknown);
        }
    }
}
