package com.example.payment.service.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class Order {
    private Long id;

    private BigDecimal totalPrice;
    private String username;
    private List<Long> productIds;
    private Map<Long, Integer> quantities;

}
