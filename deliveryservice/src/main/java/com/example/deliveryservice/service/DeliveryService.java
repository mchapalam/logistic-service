package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.InventoryRequest;
import com.example.deliveryservice.model.InventoryItem;
import com.example.deliveryservice.repository.DeliveryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryService {

    private final KafkaTemplate<String, String> stringKafkaTemplate;
    private final DeliveryRepository deliveryRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory-item-topic", groupId = "inventory-item-group", containerFactory = "inventoryItemKafkaListenerContainerFactory")
    public void consumeInventoryItems(List<InventoryRequest> inventoryItems) {
        log.info("Consumed message: {}", inventoryItems);
        inventoryItems.forEach(this::processInventoryItem);
    }

    public void processInventoryItem(InventoryRequest inventoryRequest) {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setProductId(inventoryRequest.getProductId());
        inventoryItem.setProductName(inventoryRequest.getProductName());
        inventoryItem.setQuantity(inventoryRequest.getQuantity());
        inventoryItem.setDate(LocalDate.now());
        inventoryItem.setStatus("In Inventory");

        deliveryRepository.save(inventoryItem);
        log.info("Inventory item saved: {}", inventoryItem);


        try {
            Thread.sleep(10000);

            String inventoryRequestMessage = objectMapper.writeValueAsString(inventoryRequest);
            stringKafkaTemplate.send("inventory-request-topic", inventoryRequestMessage);
        } catch (JsonProcessingException e) {
            log.error("Error serializing inventory request: {}", inventoryRequest, e);
            throw new RuntimeException("Error serializing inventory request", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
