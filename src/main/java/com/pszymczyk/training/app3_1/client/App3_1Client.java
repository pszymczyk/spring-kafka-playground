package com.pszymczyk.training.app3_1.client;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class App3_1Client {

    public static final String APP_3_1 = "app3_1";
    private final Logger logger = LoggerFactory.getLogger(App3_1Client.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App3_1Client.class);
        application.setDefaultProperties(Map.of("server.port", "8081"));
        application.run(args).close();
    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, String> template) {
        return args -> {
            ProducerRecord<String, String> record = new ProducerRecord<>(APP_3_1, "PING");
            CompletableFuture<SendResult<String, String>> replyFuture = template.send(record);
            SendResult<String, String> sendResult = replyFuture.get(10, TimeUnit.SECONDS);

            logger.info("Client sent ok, {}", sendResult.getProducerRecord().value());
        };
    }
}
