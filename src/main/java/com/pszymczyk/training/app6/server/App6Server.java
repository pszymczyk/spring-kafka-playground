package com.pszymczyk.training.app6.server;

import com.pszymczyk.training.app6.DebtorsRepository;
import com.pszymczyk.training.app6.LoanApplicationDecision;
import com.pszymczyk.training.app6.LoanApplicationRequest;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Properties;

import static com.pszymczyk.training.app6.client.App6Client.APP_6_INPUT;
import static com.pszymczyk.training.app6.client.App6Client.APP_6_OUTPUT;

@SpringBootApplication
public class App6Server {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App6Server.class);
        var properties = new Properties();
        properties.put("server.port", "8082");
        properties.put("spring.kafka.listener.ack-mode", "record");
        properties.put("spring.kafka.consumer.key-deserializer", StringDeserializer.class.getName());
        properties.put("spring.kafka.consumer.value-deserializer", JsonDeserializer.class.getName());
        properties.put("spring.kafka.producer.value-serializer", JsonSerializer.class.getName());
        properties.put("spring.kafka.producer.key-serializer", StringSerializer.class.getName());
        application.setDefaultProperties(properties);
        application.run(args);
    }

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name(APP_6_INPUT)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Component
    public class MyKafkaHandler {

        private static final DebtorsRepository debtorsRepository = new DebtorsRepository();

        private final KafkaTemplate<String, LoanApplicationDecision> template;

        @Autowired
        MyKafkaHandler(KafkaTemplate<String, LoanApplicationDecision> template) {
            this.template = template;
        }

        @KafkaListener(topics = APP_6_INPUT, groupId = APP_6_INPUT)
        void handleMessages(LoanApplicationRequest loanApplicationRequest) {
        }
    }
}
