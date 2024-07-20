package com.example.payment.service.config.utils;

import com.example.payment.service.dto.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class OrderRequestSerializer implements Serializer<OrderRequest> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, OrderRequest data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize OrderRequest", e);
        }
    }

    @Override
    public void close() {
    }
}
