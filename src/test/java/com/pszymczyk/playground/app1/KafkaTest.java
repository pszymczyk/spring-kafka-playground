package com.pszymczyk.playground.app1;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EmbeddedKafka
abstract class KafkaTest {

    @Autowired
    protected EmbeddedKafkaBroker kafkaEmbedded;

    @Value("${spring.embedded.kafka.brokers}")
    protected String brokerAddresses;

    protected KafkaTemplate<String, String> kafkaTemplate;

    @BeforeAll
    void beforeAll() {
        kafkaTemplate = new KafkaTemplate<>(null);
    }
}
