package com.example.deliveryservice.config.deserializer;

import com.example.deliveryservice.dto.InventoryRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.List;
import java.util.Map;

public class InventoryRequestListDeserializer implements Deserializer<List<InventoryRequest>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public List<InventoryRequest> deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, new TypeReference<List<InventoryRequest>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize list of InventoryRequest", e);
        }
    }

    @Override
    public void close() {
    }
}
