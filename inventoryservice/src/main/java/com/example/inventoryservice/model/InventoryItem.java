package com.example.inventoryservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

    private Long orderId;

    public InventoryItem(Long productId, int quantity, Long orderId) {
        this.productId = productId;
        this.quantity = quantity;
        this.orderId = orderId;
    }
}
