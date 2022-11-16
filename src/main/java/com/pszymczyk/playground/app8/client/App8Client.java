package com.pszymczyk.playground.app8.client;

import com.pszymczyk.playground.app8.server.App8Server;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Properties;

@SpringBootApplication
public class App8Client {

    public static final String APP_8 = "app8";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App8Client.class);
        var properties = new Properties();
        properties.put("spring.kafka.producer.batch-size", "1");
        application.setDefaultProperties(properties);
        application.run(args).close();

    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, String> template) {
        return args -> {
            template.send(new ProducerRecord<>(APP_8, null, "PING"));
            template.send(new ProducerRecord<>(APP_8, null, "PING"));
            template.send(new ProducerRecord<>(APP_8, null, "PING"));
            template.send(new ProducerRecord<>(APP_8, null, "PING"));
            template.send(new ProducerRecord<>(APP_8, "fas", "PING"));
            template.send(new ProducerRecord<>(APP_8, "qwerfewq", "PING"));
            template.send(new ProducerRecord<>(APP_8, "dsgsbdfs", "PING"));
            template.send(new ProducerRecord<>(APP_8, "dfghdfd", "PING"));
            template.send(new ProducerRecord<>(APP_8, "1234", "PING"));
            template.send(new ProducerRecord<>(APP_8, "terw", "PING"));
            template.send(new ProducerRecord<>(APP_8, "hfghfgee", "PING"));
            template.send(new ProducerRecord<>(APP_8, "xcvcxcv", "PING"));
            template.send(new ProducerRecord<>(APP_8, "fasfds", "PING"));
            template.send(new ProducerRecord<>(APP_8, "fgsdgfd", "PING"));
        };
    }
}
