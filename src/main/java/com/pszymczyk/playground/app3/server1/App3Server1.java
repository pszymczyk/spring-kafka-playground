package com.pszymczyk.playground.app3.server1;

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

import static com.pszymczyk.playground.app3.client.App3Client.APP_3_REPLIES;
import static com.pszymczyk.playground.app3.client.App3Client.APP_3_REQUESTS;

@SpringBootApplication
public class App3Server1 {

    private final Logger logger = LoggerFactory.getLogger(App3Server1.class);

    public static void main(String[] args) {
        SpringApplication.run(App3Server1.class, args);
    }

    @KafkaListener(id = "app3-server-1", topics = APP_3_REQUESTS)
    @SendTo
    public String listen(String in) {
        logger.info("Server received {}", in);
        return "PONG from server 1";
    }

    @Bean
    public NewTopic app2Requests() {
        return TopicBuilder.name(APP_3_REQUESTS)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic app2Replies() {
        return TopicBuilder.name(APP_3_REPLIES)
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
