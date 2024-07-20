package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;


    @PostMapping
    public Order createOrder( @RequestBody OrderRequest orderRequest, @AuthenticationPrincipal Jwt jwt) {
        return orderService.createOrder(orderRequest, jwt);
    }

    @PatchMapping("/{orderId}")
    public Order updateOrderStatus(@PathVariable Long orderId, @RequestBody String status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @GetMapping("/{orderId}")
    public Order getOrderDetails(@PathVariable Long orderId) {
        return orderService.getOrderDetails(orderId);
    }

}
