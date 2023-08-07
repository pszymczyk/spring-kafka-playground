package com.pszymczyk.training.app3.server;

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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

import static com.pszymczyk.training.app3.client.App3Client.APP_3;

@SpringBootApplication
public class App3Server {

    private static final Logger logger = LoggerFactory.getLogger(App3Server.class);

    public static void main(String[] args) {
        SpringApplication.run(App3Server.class, args);
    }

    @Component
    public class MyKafkaHandler {

        void handleMessages(ConsumerRecord<String, String> message) {
            logger.info("Handle, message. Record headers: ");
            message.headers().forEach(header -> logger.info("{}:{}", header.key(), new String(header.value())));
            Utils.failSometimes();
        }

        public void processMessage(String message) {
            logger.info("Dlt received message {}", message);
        }
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> myKafkaContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            KafkaTemplate<String, String> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public NewTopic app6Messages() {
        return TopicBuilder.name(APP_3)
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
