package com.pszymczyk.training.app1.client;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@SpringBootApplication
public class App1Client {

    public static final String APP_1 = "app1";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App1Client.class);
        var properties = new Properties();
        properties.put("spring.kafka.producer.batch-size", "1");
        application.setDefaultProperties(properties);
        application.run(args).close();

    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, String> template) {
        return args -> {
            template.send(new ProducerRecord<>(APP_1, null, "PING"));
            template.send(new ProducerRecord<>(APP_1, null, "PING"));
            template.send(new ProducerRecord<>(APP_1, null, "PING"));
            template.send(new ProducerRecord<>(APP_1, null, "PING"));
            template.send(new ProducerRecord<>(APP_1, "fas", "PING"));
            template.send(new ProducerRecord<>(APP_1, "qwerfewq", "PING"));
            template.send(new ProducerRecord<>(APP_1, "dsgsbdfs", "PING"));
            template.send(new ProducerRecord<>(APP_1, "dfghdfd", "PING"));
            template.send(new ProducerRecord<>(APP_1, "1234", "PING"));
            template.send(new ProducerRecord<>(APP_1, "terw", "PING"));
            template.send(new ProducerRecord<>(APP_1, "hfghfgee", "PING"));
            template.send(new ProducerRecord<>(APP_1, "xcvcxcv", "PING"));
            template.send(new ProducerRecord<>(APP_1, "fasfds", "PING"));
            template.send(new ProducerRecord<>(APP_1, "fgsdgfd", "PING"));
        };
    }
}
