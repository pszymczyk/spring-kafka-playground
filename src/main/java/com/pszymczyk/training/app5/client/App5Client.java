package com.pszymczyk.training.app5.client;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class App5Client {

    public static final String APP_5 = "app5";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App5Client.class);
        application.setDefaultProperties(Map.of(
                "server.port", "8081"
        ));
        application.run(args).close();
    }

    @Bean
    public ApplicationRunner runner(TransactionalSender transactionalSender) {
        return args -> transactionalSender.send("PING");
    }

    @Component
    public static class TransactionalSender {

        private final Logger logger = LoggerFactory.getLogger(TransactionalSender.class);

        private final KafkaTemplate<String, String> kafkaTemplate;

        @Autowired
        TransactionalSender(KafkaTemplate<String, String> kafkaTemplate) {
            this.kafkaTemplate = kafkaTemplate;
        }

        public void send(String value) throws ExecutionException, InterruptedException, TimeoutException {
        }
    }
}
