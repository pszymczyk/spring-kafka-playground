package com.pszymczyk.playground.app2.server;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class App2Server {

    private final Logger logger = LoggerFactory.getLogger(App2Server.class);

    public static void main(String[] args) {
        SpringApplication.run(App2Server.class, args);
    }

    @KafkaListener(id = "app2-server", topics = "app2-requests")
    @SendTo
    public String listen(String in) {
        logger.info("Server received {}", in);
        return "PONG";
    }


    @Bean
    public NewTopic app2Requests() {
        return TopicBuilder.name("app2-requests")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic app2Replies() {
        return TopicBuilder.name("app2-replies")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Component
    public class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

        @Override
        public void customize(ConfigurableWebServerFactory factory) {
            factory.setPort(8081);
        }
    }
}
