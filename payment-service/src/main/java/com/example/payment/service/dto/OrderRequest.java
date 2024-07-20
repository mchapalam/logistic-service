package com.example.payment.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class OrderRequest {
    private Long orderId;
    private BigDecimal totalPrice;
    private String status;
    private List<Long> productIds;
    private Map<Long, Integer> quantities;
    private String username;
}