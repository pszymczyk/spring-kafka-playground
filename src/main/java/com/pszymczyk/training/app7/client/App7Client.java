package com.pszymczyk.training.app7.client;

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
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class App7Client {

    public static final String APP_7_REQUESTS = "app7-requests";
    public static final String APP_7_REPLIES = "app7-replies";
    private final Logger logger = LoggerFactory.getLogger(App7Client.class);

    public static void main(String[] args) {
        SpringApplication.run(App7Client.class, args).close();
    }

    @Bean
    public ApplicationRunner runner(ReplyingKafkaTemplate<String, String, String> template) {

        return args -> {
            if (!template.waitForAssignment(Duration.ofSeconds(10))) {
                throw new IllegalStateException("Reply container did not initialize");
            }
            ProducerRecord<String, String> record = new ProducerRecord<>(APP_7_REQUESTS, "PING");
            RequestReplyFuture<String, String, String> replyFuture = template.sendAndReceive(record, Duration.ofMinutes(5));
            logger.info("Client received response with headers:");
            replyFuture.get(6, TimeUnit.MINUTES).headers().forEach(h -> logger.info("Header, {}:{}", h.key(), h.value()));
        };
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> replyingTemplate(
            ProducerFactory<String, String> pf,
            ConcurrentMessageListenerContainer<String, String> repliesContainer) {

        return new ReplyingKafkaTemplate<>(pf, repliesContainer);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {

        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer(APP_7_REPLIES);
        repliesContainer.getContainerProperties().setGroupId(UUID.randomUUID().toString());
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }
}
