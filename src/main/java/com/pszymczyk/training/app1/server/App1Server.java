package com.pszymczyk.training.app1.server;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.pszymczyk.training.app1.client.App1Client.APP_1;

@SpringBootApplication
public class App1Server {

    private static final Logger logger = LoggerFactory.getLogger(App1Server.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App1Server.class);
        application.setDefaultProperties(Map.of(
                "spring.kafka.listener.concurrency", 5,
                "server.port", "8082"
        ));
        application.run(args);
    }

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name(APP_1)
                .partitions(5)
                .replicas(1)
                .build();
    }

    @Component
    public class MyKafkaHandler {

        @KafkaListener(topics = APP_1, groupId = APP_1)
        void handleMessages(ConsumerRecord<String, String> message,
                            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
            logger.info("Handle, message. Record k: {}, partition: {}", message.key(), partition);
        }
    }
}
