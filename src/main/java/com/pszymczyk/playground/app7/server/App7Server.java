package com.pszymczyk.playground.app7.server;

import com.pszymczyk.playground.common.Utils;
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

import static com.pszymczyk.playground.app7.client.App7Client.APP_7;

@SpringBootApplication
public class App7Server {

    private static final Logger logger = LoggerFactory.getLogger(App7Server.class);

    public static void main(String[] args) {
        SpringApplication.run(App7Server.class, args);
    }

    @Component
    public class MyKafkaHandler {

        @KafkaListener(topics = APP_7, groupId = "app7", containerFactory = "myKafkaContainerFactory")
        void handleMessages(ConsumerRecord<String, String> message) {
            logger.info("Handle, message. Record headers: ");
            message.headers().forEach(header -> logger.info("{}:{}", header.key(), new String(header.value())));
            Utils.failSometimes();
        }

        @KafkaListener(topics = APP_7+ ".DLT", groupId = "app7-dlt")
        public void processMessage(String message) {
            logger.info("Dlt received message {}", message);
        }
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

    @Bean
    public NewTopic app6Messages() {
        return TopicBuilder.name(APP_7)
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
