package com.pszymczyk.kafka;

import com.pszymczyk.commands.OrderCommand;
import com.pszymczyk.events.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.List;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfiguration {

    private final List<String> bootstrapServers;
    private final String groupId;

    public KafkaConfiguration(@Value("${spring.kafka.bootstrap-servers}") List<String> bootstrapServers,
                       @Value("${spring.kafka.consumer.group-id}") String groupId) {
        this.bootstrapServers = bootstrapServers;
        this.groupId = groupId;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCommand> myKafkaContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, OrderCommand> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    ConsumerFactory<String, OrderCommand> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest",
                ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed",
                JsonDeserializer.TRUSTED_PACKAGES, "com.pszymczyk.commands",
                JsonDeserializer.USE_TYPE_INFO_HEADERS, false,
                JsonDeserializer.VALUE_DEFAULT_TYPE, OrderCommand.class));
    }


    @Bean
    public KafkaTemplate<String, OrderEvent> orderEventKafkaTemplate() {
        return new KafkaTemplate<>(orderEventProducerFactory());
    }

    @Bean
    public ProducerFactory<String, OrderEvent> orderEventProducerFactory() {

        Map<String, Object> configProps = Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        final DefaultKafkaProducerFactory<String, OrderEvent> stringOrderEventDefaultKafkaProducerFactory = new DefaultKafkaProducerFactory<>(configProps);
        stringOrderEventDefaultKafkaProducerFactory.setTransactionIdPrefix("tx-");
        return stringOrderEventDefaultKafkaProducerFactory;
    }
//
//    @Bean
//    public ProducerFactory<String, OrderCommand> orderCommandProducerFactory(KafkaProperties kafkaProperties) {
//
//        Map<String, Object> configProps = Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
//                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
//                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return new DefaultKafkaProducerFactory<>(configProps);
//    }
//
//    @Bean
//    public KafkaTemplate<String, OrderCommand> orderCommandKafkaTemplate(ProducerFactory<String, OrderCommand> producerFactory) {
//        return new KafkaTemplate<>(producerFactory);
//    }
}
