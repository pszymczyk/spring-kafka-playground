package com.pszymczyk.training.app3_1.server;

import com.pszymczyk.training.app1.server.App1Server;
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

import java.time.Duration;
import java.util.Properties;

import static com.pszymczyk.training.app3_1.client.App3_1Client.APP_3_1;

@SpringBootApplication
public class App3_1Server {

    private static final Logger logger = LoggerFactory.getLogger(App3_1Server.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App3_1Server.class);
        var properties = new Properties();
        properties.put("spring.kafka.consumer.properties.max.poll.interval.ms", 60_000);
        application.setDefaultProperties(properties);
        application.run(args);
    }

    @Component
    public class MyKafkaHandler {

        @KafkaListener(topics = APP_3_1, groupId = APP_3_1, containerFactory = "myKafkaContainerFactory")
        void handleMessages(ConsumerRecord<String, String> message) {
            logger.info("Handle, message. Offset: {}, Record headers: ", message.offset());
            message.headers().forEach(header -> logger.info("{}:{}", header.key(), new String(header.value())));
            Utils.failSometimes();
        }

        @KafkaListener(topics = APP_3_1 + ".DLT", groupId = APP_3_1 + ".DLT")
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
                new FixedBackOff(Duration.ofMinutes(3).toMillis(), 5L));

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        factory.setConcurrency(3);
        return factory;
    }

    @Bean
    public NewTopic app6Messages() {
        return TopicBuilder.name(APP_3_1)
                .partitions(2)
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
