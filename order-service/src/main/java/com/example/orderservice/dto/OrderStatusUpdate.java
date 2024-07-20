package com.example.orderservice.dto;

import com.example.orderservice.model.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusUpdate {
    private Long orderId;
    private OrderStatus status;
}