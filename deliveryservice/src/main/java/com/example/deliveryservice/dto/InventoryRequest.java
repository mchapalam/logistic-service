package com.example.deliveryservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryRequest {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

    private Long orderId;
}
