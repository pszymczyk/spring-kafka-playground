package com.pszymczyk.playground.app8.server;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static com.pszymczyk.playground.app8.client.App8Client.APP_8;

@SpringBootApplication
public class App8Server {

    private static final Logger logger = LoggerFactory.getLogger(App8Server.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App8Server.class);
        var properties = new Properties();
        properties.put("kafka.listener.concurrency", 8);
        application.setDefaultProperties(properties);
        application.run(args);
    }

    @Component
    public class MyKafkaHandler {

        @KafkaListener(topics = APP_8, groupId = "app8")
        void handleMessages(ConsumerRecord<String, String> message,
                            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
            logger.info("Handle, message. Record k: {}, partition: {}", message.key(), partition);
        }
    }

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name(APP_8)
                .partitions(5)
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
