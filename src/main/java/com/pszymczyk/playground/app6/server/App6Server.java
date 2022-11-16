package com.pszymczyk.playground.app6.server;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class App6Server {

    private final Logger logger = LoggerFactory.getLogger(App6Server.class);

    public static void main(String[] args) {
        SpringApplication.run(App6Server.class, args);
    }

    @Component
    public class MyKafkaHandler {

        @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 3000))
        @KafkaListener(topics = "app6", groupId = "app6")
        void handleMessages(ConsumerRecord<String, String> message) {
            logger.error("Handle error message {}", message.value());
            logger.info("Error message headers {}", message.headers());
            throw new RuntimeException("Oooops");
        }

        @DltHandler
        public void processMessage(String message) {
            logger.info("Dlt received message {}", message);
        }

    }

    @Bean
    public NewTopic app6Messages() {
        return TopicBuilder.name("app6")
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
