package com.pszymczyk.training.app7.server;

import com.pszymczyk.training.app7.client.App7Client;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.messaging.handler.annotation.SendTo;

import java.util.Map;

@SpringBootApplication
public class App7Server {

    private final Logger logger = LoggerFactory.getLogger(App7Server.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App7Server.class);
        application.setDefaultProperties(Map.of("server.port", "8082"));
        application.run(args);
    }

    @KafkaListener(id = "app8-server", topics = App7Client.APP_7_REQUESTS)
    @SendTo
    public String listen(ConsumerRecord<String, String> consumerRecord) {
        logger.info("Server received request with headers:");
        consumerRecord.headers().forEach(h -> logger.info("Server received message with headers {}:{}", h.key(), h.value()));
        return "PONG";
    }


    @Bean
    public NewTopic app2Requests() {
        return TopicBuilder.name(App7Client.APP_7_REQUESTS)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic app2Replies() {
        return TopicBuilder.name(App7Client.APP_7_REPLIES)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
