package com.pszymczyk.training.app6.client;

import com.pszymczyk.training.app6.LoanApplicationRequest;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.math.BigDecimal;
import java.util.Properties;

@SpringBootApplication
public class App6Client {

    public static final String APP_6_INPUT = "app6-input";
    public static final String APP_6_OUTPUT = "app6-output";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App6Client.class);
        var properties = new Properties();
        properties.put("spring.kafka.producer.value-serializer", JsonSerializer.class.getName());
        properties.put("spring.kafka.producer.key-serializer", StringSerializer.class.getName());
        application.setDefaultProperties(properties);
        application.run(args).close();

    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, LoanApplicationRequest> template) {
        return args -> {
            LoanApplicationRequest loanApplicationRequest = new LoanApplicationRequest();
            loanApplicationRequest.setRequester("jan k.");
            loanApplicationRequest.setAmount(new BigDecimal("100"));
            template.send(APP_6_INPUT, loanApplicationRequest);
        };
    }
}
