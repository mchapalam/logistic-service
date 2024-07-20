package com.example.inventoryservice.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    private String productId;
    private String orderId;
    private int quantity;
    private BigDecimal price;
}
