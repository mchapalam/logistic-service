package com.example.inventoryservice.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InventoryCheckRequest {
    private Long orderId;
    private List<Long> productIds;
    private Map<Long, Integer> quantities;
}