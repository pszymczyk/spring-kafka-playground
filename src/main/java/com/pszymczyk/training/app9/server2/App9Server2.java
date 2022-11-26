package com.pszymczyk.training.app9.server2;

import com.pszymczyk.training.app9.client.App9Client;
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
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class App9Server2 {

    private final Logger logger = LoggerFactory.getLogger(App9Server2.class);

    public static void main(String[] args) {
        SpringApplication.run(App9Server2.class, args);
    }

    @KafkaListener(id = "app9-server-2", topics = App9Client.APP_9_REQUESTS)
    @SendTo
    public String listen(ConsumerRecord<String, String> consumerRecord) {
        logger.info("Server received request with headers:");
        consumerRecord.headers().forEach(h -> logger.info("Server received message with headers {}:{}", h.key(), h.value()));
        return "PONG from server 2";
    }

    @Bean
    public NewTopic app9Requests() {
        return TopicBuilder.name(App9Client.APP_9_REQUESTS)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic app9Replies() {
        return TopicBuilder.name(App9Client.APP_9_REPLIES)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Component
    public class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

        @Override
        public void customize(ConfigurableWebServerFactory factory) {
            factory.setPort(8082);
        }
    }
}
