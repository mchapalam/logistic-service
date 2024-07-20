package com.example.inventoryservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryItemRequest {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

    private Long orderId;
}
