package com.pszymczyk.training.app1.client;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@SpringBootApplication
public class App1Client {

    public static final String APP_1 = "app1";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App1Client.class);
        application.setDefaultProperties(Map.of(
                "spring.kafka.producer.batch-size", "1",
                "server.port", "8081"
        ));
        application.run(args).close();
    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, String> template) {
        return args -> {
            template.send(new ProducerRecord<>(APP_1, null, "PING")).get();
            template.send(new ProducerRecord<>(APP_1, null, "PING")).get();
            template.send(new ProducerRecord<>(APP_1, null, "PING")).get();
            template.send(new ProducerRecord<>(APP_1, null, "PING")).get();
            template.send(new ProducerRecord<>(APP_1, "fas", "PING")).get();
            template.send(new ProducerRecord<>(APP_1, "qwerfewq", "PING")).get();
            template.send(new ProducerRecord<>(APP_1, "dsgsbdfs", "PING")).get();
            template.send(new ProducerRecord<>(APP_1, "dfghdfd", "PING")).get();
            template.send(new ProducerRecord<>(APP_1, "1234", "PING")).get();
            template.send(new ProducerRecord<>(APP_1, "terw", "PING")).get();
            template.send(new ProducerRecord<>(APP_1, "hfghfgee", "PING")).get();
            template.send(new ProducerRecord<>(APP_1, "xcvcxcv", "PING")).get();
            template.send(new ProducerRecord<>(APP_1, "fasfds", "PING")).get();
            template.send(new ProducerRecord<>(APP_1, "fgsdgfd", "PING")).get();
        };
    }
}
