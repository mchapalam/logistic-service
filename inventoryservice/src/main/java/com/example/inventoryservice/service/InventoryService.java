package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryCheckRequest;
import com.example.inventoryservice.dto.InventoryCheckResponse;
import com.example.inventoryservice.dto.InventoryItemRequest;
import com.example.inventoryservice.dto.OrderResponse;
import com.example.inventoryservice.model.InventoryItem;
import com.example.inventoryservice.model.OrderItem;
import com.example.inventoryservice.repository.InventoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
    private final KafkaTemplate<String, OrderResponse> orderRequestKafkaTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, List<InventoryItem>> inventoryItemKafkaTemplate;

    private final ObjectMapper objectMapper;
    private final InventoryRepository inventoryRepository;

    private final Map<Long, CompletableFuture<OrderResponse>> futureMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "order-response-topic", groupId = "string-group")
    public void handleOrderResponse(String message) {
        try {
            OrderResponse orderResponse = objectMapper.readValue(message, OrderResponse.class);
            CompletableFuture<OrderResponse> future = futureMap.get(orderResponse.getOrderId());

            log.info("Handling order response for order ID: {}", orderResponse.getOrderId());

            if ("PAID".equals(orderResponse.getStatus()) || "INVENTMENT_FAILED".equals(orderResponse.getStatus())) {
                log.info("Checking inventory for order ID: {}", orderResponse.getOrderId());

                List<InventoryItem> shortages = checkInventory(orderResponse.getProductIds(), orderResponse.getQuantities(), orderResponse.getOrderId());

                log.info("Inventory check result for order ID {}: {}", orderResponse.getOrderId(), shortages.isEmpty());

                Thread.sleep(20000);

                if (shortages.isEmpty()) {
                    reduceInventory(orderResponse.getProductIds(), orderResponse.getQuantities());
                    orderResponse.setStatus("invented");

                    log.info("Inventory reduced for order ID: {}", orderResponse.getOrderId());
                } else {
                    orderResponse.setStatus("inventment_failed");

                    inventoryItemKafkaTemplate.send("inventory-item-topic", shortages);

                    log.warn("Not enough inventory for order ID: {}", orderResponse.getOrderId());
                }


                orderRequestKafkaTemplate.send("order-status-update-topic", orderResponse);
            } else if ("payment_failed".equals(orderResponse.getStatus())) {
                log.warn("Payment failed for order ID: {}", orderResponse.getOrderId());
            } else {
                log.error("Unexpected order status for order ID {}: {}", orderResponse.getOrderId(), orderResponse.getStatus());
            }

            if (future != null) {
                future.complete(orderResponse);
                futureMap.remove(orderResponse.getOrderId());
            } else {
                log.warn("No future found for order ID: {}", orderResponse.getOrderId());
            }

            log.info("Processed message: {}", message);
        } catch (Exception e) {
            log.error("Error processing order response: {}", message, e);
            throw new RuntimeException("Failed to process order response", e);
        }
    }

    private List<InventoryItem> checkInventory(List<Long> productIds, Map<Long, Integer> quantities, Long orderId) {
        List<InventoryItem> shortages = new ArrayList<>();

        for (Long productId : productIds) {
            Integer availableQuantity = inventoryRepository.getAvailableQuantity(productId);
            log.info("Checking inventory for product ID {}: Available quantity = {}", productId, availableQuantity);

            int requiredQuantity = quantities.get(productId);
            int shortage = requiredQuantity - (availableQuantity == null ? 0 : availableQuantity);

            if (shortage > 0) {
                log.warn("Insufficient inventory for product ID {}: Required = {}, Available = {}, Shortage = {}",
                        productId, requiredQuantity, availableQuantity, shortage);
                shortages.add(new InventoryItem(productId, shortage, orderId));
            }
        }

        if (!shortages.isEmpty()) {
            log.warn("Inventory shortages found: {}", shortages);
        }

        return shortages;
    }

    @KafkaListener(topics = "inventory-request-topic", groupId = "string-group")
    public void handlePaymentSuccess(String message) {
        try {
            InventoryItem inventoryItem = objectMapper.readValue(message, InventoryItem.class);

            InventoryItem inventoryItemUpdate = inventoryRepository.findByProductId(inventoryItem.getProductId());

            inventoryItemUpdate.setQuantity(
                    inventoryItem.getQuantity() + inventoryItemUpdate.getQuantity()
            );

            inventoryRepository.save(inventoryItemUpdate);

            kafkaTemplate.send("order-delivery-topic", inventoryItem.getOrderId().toString());

            log.info("{}", inventoryItem);

        } catch (JsonProcessingException e) {

            log.error("Error processing order response: {}", message, e);
            throw new RuntimeException(e);
        }
    }

    private void reduceInventory(List<Long> productIds, Map<Long, Integer> quantities) {
        for (Long productId : productIds) {
            Integer quantityToReduce = quantities.get(productId);
            inventoryRepository.reduceStock(productId, quantityToReduce);
        }
    }

    public void checkInventoryAndRespond(Long orderId) {
        kafkaTemplate.send("order-request-topic", String.valueOf(orderId));
    }

    public void addInventoryItems(List<InventoryItemRequest> inventoryItems) {
        for (InventoryItemRequest itemRequest : inventoryItems) {
            InventoryItem inventoryItem = inventoryRepository.findByProductId(itemRequest.getProductId());
            if (inventoryItem == null) {
                inventoryItem = new InventoryItem();
                inventoryItem.setProductId(itemRequest.getProductId());
                inventoryItem.setProductName(itemRequest.getProductName());
                inventoryItem.setQuantity(itemRequest.getQuantity());
                inventoryItem.setPrice(itemRequest.getPrice());
            } else {
                inventoryItem.setQuantity(inventoryItem.getQuantity() + itemRequest.getQuantity());
            }
            inventoryRepository.save(inventoryItem);
        }
    }

    public List<InventoryItem> getAllInventoryItems() {
        return inventoryRepository.findAll();
    }
}
