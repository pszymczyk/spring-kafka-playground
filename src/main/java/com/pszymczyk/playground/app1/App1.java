package com.pszymczyk.playground.app1;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
@SpringBootApplication
public class App1 {

    public static final String APP_1_MESSAGES = "app1-messages";
    public static final String APP_1_DEFAULT = "app1-default";

    public static void main(String[] args) {
        SpringApplication.run(App1.class, args).close();
    }

    @Bean
    public RoutingKafkaTemplate routingTemplate(GenericApplicationContext context,
                                                ProducerFactory<Object, Object> pf) {

        var configs = new HashMap<>(pf.getConfigurationProperties());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        var jsonPF = new DefaultKafkaProducerFactory<>(configs);
        context.registerBean(DefaultKafkaProducerFactory.class, "jsonProducerFactory", jsonPF);

        Map<Pattern, ProducerFactory<Object, Object>> map = new LinkedHashMap<>();
        map.put(Pattern.compile(APP_1_MESSAGES), jsonPF);
        map.put(Pattern.compile(".+"), pf);
        return new RoutingKafkaTemplate(map);
    }

    @Bean
    public ApplicationRunner runner(RoutingKafkaTemplate routingKafkaTemplate) {
        return args -> {
            routingKafkaTemplate.send(APP_1_MESSAGES, new Message("sender", "receiver", "Hello world!"));
            routingKafkaTemplate.send(APP_1_DEFAULT, "Plain text Hello World!");
        };
    }

    @Bean
    public KafkaAdmin.NewTopics topics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(APP_1_MESSAGES)
                        .partitions(1)
                        .replicas(1)
                        .build(),
                TopicBuilder.name(APP_1_DEFAULT)
                        .partitions(1)
                        .replicas(1)
                        .build());
    }
}
