package com.pszymczyk.training.app4.server;

import com.pszymczyk.training.common.Utils;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.stereotype.Component;

import static com.pszymczyk.training.app4.client.App4Client.APP_4;

@SpringBootApplication
public class App4Server {

    private static final Logger logger = LoggerFactory.getLogger(App4Server.class);

    public static void main(String[] args) {
        SpringApplication.run(App4Server.class, args);
    }

    @Component
    public class MyKafkaHandler {

        @KafkaListener(topics = APP_4, groupId = APP_4)
        void handleMessages(ConsumerRecord<String, String> message) {
            logger.info("Handle, message. Record headers: ");
            message.headers().forEach(header -> logger.info("{}:{}", header.key(), new String(header.value())));
            Utils.failSometimes();
        }

        @DltHandler
        public void processMessage(String message) {
            logger.info("Dlt received message {}", message);
        }
    }

    @Bean
    public NewTopic app4Messages() {
        return TopicBuilder.name(APP_4)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public RetryTopicConfiguration myOtherRetryTopic(KafkaTemplate<String, String> template) {
        return RetryTopicConfigurationBuilder
                .newInstance()
                .exponentialBackoff(1000, 2, 10_000)
                .maxAttempts(4)
                .create(template);
    }

    @Component
    public class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

        @Override
        public void customize(ConfigurableWebServerFactory factory) {
            factory.setPort(8081);
        }
    }

}
