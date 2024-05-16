package com.pszymczyk.training.app3.server;

import com.pszymczyk.training.common.Utils;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

import java.util.Map;

import static com.pszymczyk.training.app3.client.App3Client.APP_3;

@SpringBootApplication
public class App3Server {

    private static final Logger logger = LoggerFactory.getLogger(App3Server.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App3Server.class);
        application.setDefaultProperties(Map.of("server.port", "8082"));
        application.run(args);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> myKafkaContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            KafkaTemplate<String, String> kafkaTemplate) {

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate),
                new FixedBackOff(2000L, 2L));

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Component
    public class MyKafkaHandler {

        @KafkaListener(topics = APP_3, groupId = APP_3, containerFactory = "myKafkaContainerFactory")
        void handleMessages(ConsumerRecord<String, String> message) {
            logger.info("Handle, message {}", message.value());
            logger.info("Handle, message. Record headers: ");
            message.headers().forEach(header -> logger.info("{}:{}", header.key(), new String(header.value())));
            Utils.failSometimes();
        }

        @KafkaListener(topics = APP_3 + ".DLT", groupId = APP_3 + ".DLT")
        public void processMessage(String message) {
            logger.info("Dlt received message {}", message);
        }
    }


    @Bean
    public NewTopic app6Messages() {
        return TopicBuilder.name(APP_3)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
