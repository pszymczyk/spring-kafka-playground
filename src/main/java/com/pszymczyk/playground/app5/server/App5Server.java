package com.pszymczyk.playground.app5.server;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
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
public class App5Server {

    private final Logger logger = LoggerFactory.getLogger(App5Server.class);

    public static void main(String[] args) {
        SpringApplication.run(App5Server.class, args);
    }

    @Component
    @KafkaListener(topics = "app5-messages-and-requests", containerFactory = "myKafkaContainerFactory")
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
        void handleRequests(@Payload Unknown unknown,
                            @Header(KafkaHeaders.OFFSET) long offset,
                            @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
            logger.info("Server received unknown message {},{},{}", offset, partitionId, topic);
        }
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageAndRequest> myKafkaContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, MessageAndRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    ConsumerFactory<String, MessageAndRequest> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, "app5",
                JsonDeserializer.TRUSTED_PACKAGES, "com.pszymczyk.playground.app5",
                JsonDeserializer.USE_TYPE_INFO_HEADERS, false,
                JsonDeserializer.VALUE_DEFAULT_TYPE, MessageAndRequest.class));
    }

    @Bean
    public NewTopic app4Requests() {
        return TopicBuilder.name("app5-messages-and-requests")
                .partitions(1)
                .replicas(1)
                .build();
    }


    @Component
    public class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

        @Override
        public void customize(ConfigurableWebServerFactory factory) {
            factory.setPort(8081);
        }
    }

}
