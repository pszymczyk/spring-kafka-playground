package com.pszymczyk.training.app5.server;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

import static com.pszymczyk.training.app5.client.App5Client.APP_5;

@SpringBootApplication
public class App5Server {

    private static final Logger logger = LoggerFactory.getLogger(App5Server.class);

    public static void main(String[] args) {
        SpringApplication.run(App5Server.class, args);
    }

    @Component
    public class MyKafkaHandler {

        @KafkaListener(topics = APP_5, groupId = APP_5)
        void handleMessages(ConsumerRecord<String, String> message) {
            logger.info("Server handling message {}:{}:{} ", message.value(), message.topic(), message.offset());
        }

        @KafkaListener(topics = "__transaction_state", groupId = APP_5 + ".txstate")
        void handleTransactions(ConsumerRecord<String, String> message) {
            logger.info("Server handle transaction state changed {}:{} ", message.key(), message.value());
        }
    }

    @Bean
    public NewTopic app5Messages() {
        return TopicBuilder.name(APP_5)
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
