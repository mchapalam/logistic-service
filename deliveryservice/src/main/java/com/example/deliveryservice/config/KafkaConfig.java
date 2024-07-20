package com.example.deliveryservice.config;

import com.example.deliveryservice.config.deserializer.InventoryRequestListDeserializer;
import com.example.deliveryservice.dto.InventoryRequest;
import com.example.deliveryservice.model.InventoryItem;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, String> stringStringProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringStringProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, List<InventoryRequest>> inventoryItemConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-item-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, InventoryRequestListDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new InventoryRequestListDeserializer())
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<InventoryRequest>> inventoryItemKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, List<InventoryRequest>> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inventoryItemConsumerFactory());
        return factory;
    }
}
