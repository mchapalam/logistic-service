package com.example.orderservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity(name = "app_orders")
@Data
@ToString
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalPrice;
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String username;

    @ElementCollection
    private List<Long> productIds;

    @ElementCollection
    private Map<Long, Integer> quantities;

    @Lob
    private String statusHistory;

    public void addStatusHistory(OrderStatus orderStatus){
        if (this.statusHistory == null) {
            this.statusHistory = "";
        }
        this.statusHistory += "[" + LocalDateTime.now() + "] " + orderStatus.name() + "\n";
    }
}
