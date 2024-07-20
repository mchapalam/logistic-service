package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long orderId;
    private BigDecimal totalPrice;
    private String status;
    private List<Long> productIds;
    private Map<Long, Integer> quantities;
    private String username;
}