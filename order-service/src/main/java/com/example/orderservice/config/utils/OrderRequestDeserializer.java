package com.example.orderservice.config.utils;


import com.example.orderservice.dto.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class OrderRequestDeserializer implements Deserializer<OrderRequest> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public OrderRequest deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, OrderRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize OrderRequest", e);
        }
    }

    @Override
    public void close() {
    }
}