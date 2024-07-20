package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private String status;
    private BigDecimal totalPrice;
    private List<Long> productIds;
    private Map<Long, Integer> quantities;
    private String username;
    private String statusHistory;
}
