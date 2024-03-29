package com.pszymczyk.training.app2.client;

import com.pszymczyk.training.app2.server.Message;
import com.pszymczyk.training.app2.server.Request;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SpringBootApplication
public class App2Client {


    public static final String APP_2_MESSAGES_AND_REQUESTS = "app2-messages-and-requests";

    public static void main(String[] args) {
        SpringApplication.run(App2Client.class, args).close();
    }

    @Bean
    public RoutingKafkaTemplate routingTemplate(GenericApplicationContext context,
                                                ProducerFactory<Object, Object> pf) {

        Map<String, Object> configs = new HashMap<>(pf.getConfigurationProperties());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        var jsonPF = new DefaultKafkaProducerFactory<>(configs);
        context.registerBean(DefaultKafkaProducerFactory.class, "jsonProducerFactory", jsonPF);

        Map<Pattern, ProducerFactory<Object, Object>> map = new LinkedHashMap<>();
        map.put(Pattern.compile(APP_2_MESSAGES_AND_REQUESTS), jsonPF);
        map.put(Pattern.compile(".+"), pf);
        return new RoutingKafkaTemplate(map);
    }

    @Bean
    public ApplicationRunner runner(RoutingKafkaTemplate routingKafkaTemplate) {
        return args -> {
            routingKafkaTemplate.send(APP_2_MESSAGES_AND_REQUESTS, new Message("sender", "receiver", "Hello world!"));
            routingKafkaTemplate.send(APP_2_MESSAGES_AND_REQUESTS, new Request("id", "some funny request"));
            routingKafkaTemplate.send(APP_2_MESSAGES_AND_REQUESTS, Map.of("type", "something-not-supported"));
        };
    }
}
