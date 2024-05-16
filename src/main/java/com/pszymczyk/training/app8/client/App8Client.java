package com.pszymczyk.training.app8.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.AggregatingReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class App8Client {

    public static final String APP_8_REQUESTS = "app8-requests";
    public static final String APP_8_REPLIES = "app8-replies";
    private final Logger logger = LoggerFactory.getLogger(App8Client.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App8Client.class);
        application.setDefaultProperties(Map.of("server.port", "8081"));
        application.run(args).close();
    }

    @Bean
    public ApplicationRunner runner(AggregatingReplyingKafkaTemplate<String, String, String> template) {
        return args -> {
            if (!template.waitForAssignment(Duration.ofSeconds(10))) {
                throw new IllegalStateException("Reply container did not initialize");
            }
        };
    }

    @Bean
    public AggregatingReplyingKafkaTemplate<String, String, String> replyingTemplate(
            ProducerFactory<String, String> pf,
            ConcurrentMessageListenerContainer<String, Collection<ConsumerRecord<String, String>>> repliesContainer) {

        return null;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Collection<ConsumerRecord<String, String>>> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, Collection<ConsumerRecord<String, String>>> containerFactory) {

        return null;
    }
}
