package com.pszymczyk.playground.app3.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.AggregatingReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;

@SpringBootApplication
public class App3Client {

    private final Logger logger = LoggerFactory.getLogger(App3Client.class);

    public static void main(String[] args) {
        SpringApplication.run(App3Client.class, args).close();
    }

    @Bean
    public ApplicationRunner runner(AggregatingReplyingKafkaTemplate<String, String, String> template) {
        return args -> {
            if (!template.waitForAssignment(Duration.ofSeconds(10))) {
                throw new IllegalStateException("Reply container did not initialize");
            }
            ProducerRecord<String, String> record = new ProducerRecord<>("app3-requests", "PING");
            RequestReplyFuture<String, String, Collection<ConsumerRecord<String, String>>> replyFuture = template.sendAndReceive(record, Duration.ofMinutes(5));
            SendResult<String, String> sendResult = replyFuture.getSendFuture().get(10, TimeUnit.SECONDS);

            logger.info("Client sent ok, {}", sendResult.getProducerRecord().value());

            ConsumerRecord<String, Collection<ConsumerRecord<String, String>>> replies = replyFuture.get(6, TimeUnit.MINUTES);

            replies.value().forEach(reply -> logger.info("Client got reply: {}", reply.value()));

        };
    }

    @Bean
    public AggregatingReplyingKafkaTemplate<String, String, String> replyingTemplate(
            ProducerFactory<String, String> pf,
            ConcurrentMessageListenerContainer<String, Collection<ConsumerRecord<String, String>>> repliesContainer) {

        return new AggregatingReplyingKafkaTemplate<>(pf, repliesContainer, (consumerRecords, aBoolean) -> consumerRecords.size() > 1);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Collection<ConsumerRecord<String, String>>> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, Collection<ConsumerRecord<String, String>>> containerFactory) {

        ConcurrentMessageListenerContainer<String, Collection<ConsumerRecord<String, String>>> repliesContainer =
                containerFactory.createContainer("app3-replies");
        repliesContainer.getContainerProperties().setGroupId("repliesGroup");
        repliesContainer.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }
}
