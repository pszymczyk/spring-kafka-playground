package com.pszymczyk.playground.app7.client;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class App7Client {

    public static final String APP_7 = "app7";
    private final Logger logger = LoggerFactory.getLogger(App7Client.class);

    public static void main(String[] args) {
        SpringApplication.run(App7Client.class, args).close();
    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, String> template) {
        return args -> {
            ProducerRecord<String, String> record = new ProducerRecord<>(APP_7, "PING");
            ListenableFuture<SendResult<String, String>> replyFuture = template.send(record);
            SendResult<String, String> sendResult = replyFuture.get(10, TimeUnit.SECONDS);

            logger.info("Client sent ok, {}", sendResult.getProducerRecord().value());
        };
    }
}
