package com.pszymczyk.training.app2.client;

import com.pszymczyk.training.app2.server.Message;
import com.pszymczyk.training.app2.server.Request;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class App2Client {

    public static final String APP_2_MESSAGES_AND_REQUESTS = "app2-messages-and-requests";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App2Client.class);
        application.setDefaultProperties(Map.of("server.port", "8081"));
        application.run(args).close();
    }

    @Bean
    public ProducerFactory<Object, Object> jsonProducerFactory(KafkaConnectionDetails connectionDetails) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, connectionDetails.getProducer().getBootstrapServers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<Object, Object> kafkaTemplate) {
        return args -> {
            kafkaTemplate.send(APP_2_MESSAGES_AND_REQUESTS, new Message("sender", "receiver", "Hello world!")).get();
            kafkaTemplate.send(APP_2_MESSAGES_AND_REQUESTS, new Request("id", "some funny request")).get();
            kafkaTemplate.send(APP_2_MESSAGES_AND_REQUESTS, Map.of("type", "something-not-supported")).get();
        };
    }
}
