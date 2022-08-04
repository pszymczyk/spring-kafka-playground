package com.pszymczyk.playground.app5.client;

import com.pszymczyk.playground.app5.server.Message;
import com.pszymczyk.playground.app5.server.Request;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SpringBootApplication
public class App5Client {


    public static void main(String[] args) {
        SpringApplication.run(App5Client.class, args).close();
    }

    @Bean
    public RoutingKafkaTemplate routingTemplate(GenericApplicationContext context,
                                                ProducerFactory<Object, Object> pf) {

        Map<String, Object> configs = new HashMap<>(pf.getConfigurationProperties());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        var jsonPF = new DefaultKafkaProducerFactory<>(configs);
        context.registerBean(DefaultKafkaProducerFactory.class, "jsonProducerFactory", jsonPF);

        Map<Pattern, ProducerFactory<Object, Object>> map = new LinkedHashMap<>();
        map.put(Pattern.compile("app5-messages-and-requests"), jsonPF);
        map.put(Pattern.compile(".+"), pf);
        return new RoutingKafkaTemplate(map);
    }

    @Bean
    public ApplicationRunner runner(RoutingKafkaTemplate routingKafkaTemplate) {
        return args -> {
            routingKafkaTemplate.send("app5-messages-and-requests", new Message("sender", "receiver", "Hello world!"));
            routingKafkaTemplate.send("app5-messages-and-requests", new Request("sender", "receiver"));
            routingKafkaTemplate.send("app5-messages-and-requests", Map.of("type", "something-not-supported"));
        };
    }

    @Bean
    public NewTopic messages() {
        return TopicBuilder.name("app5-messages-and-requests")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic defaultTopic() {
        return TopicBuilder.name("app1-default")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
