package com.example.orderservice.service.impl;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderRequest> kafkaTemplate;
    private final KafkaTemplate<String, String> stringKafkaTemplate;
    private final ObjectMapper objectMapper;

    public Order createOrder(OrderRequest orderRequest, Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        log.info("Creating order with details: {}", orderRequest);

        Order order = new Order();
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.REGISTERED);
        order.setUsername(username);
        order.setProductIds(orderRequest.getProductIds());
        order.setQuantities(orderRequest.getQuantities());
        order.addStatusHistory(OrderStatus.REGISTERED);
        orderRequest.setUsername(username);

        Order savedOrder = orderRepository.save(order);
        orderRequest.setOrderId(savedOrder.getId());

        kafkaTemplate.send("order-topic", String.valueOf(savedOrder.getId()), orderRequest);
        log.info("Order created and sent to Kafka topic: {} with ID: {}", "order-topic", savedOrder.getId());

        return savedOrder;
    }

    public Order updateOrderStatus(Long orderId, String status) {
        log.info("Updating order status for order ID: {} to status: {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus newStatus = OrderStatus.valueOf(status);
        order.addStatusHistory(newStatus);
        order.setStatus(newStatus);

        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated to {} for order ID: {}", newStatus, orderId);

        return updatedOrder;
    }

    @KafkaListener(topics = "order-request-topic", groupId = "order-group")
    public void receiveOrderRequest(String message) {
        try {
            log.info("Received order request message: {}", message);
            String cleanMessage = message.replaceAll("^\"|\"$", "");
            Long orderId = Long.parseLong(cleanMessage);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            Hibernate.initialize(order.getProductIds());
            Hibernate.initialize(order.getQuantities());

            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setOrderId(order.getId());
            orderRequest.setTotalPrice(order.getTotalPrice());
            orderRequest.setProductIds(order.getProductIds());
            orderRequest.setQuantities(order.getQuantities());
            orderRequest.setUsername(order.getUsername());
            orderRequest.setStatus(order.getStatus().toString());

            log.debug("Sending order request message to Kafka topic: {} with details: {}", "order-response-topic", orderRequest);
            String orderRequestMessage = objectMapper.writeValueAsString(orderRequest);
            stringKafkaTemplate.send("order-response-topic", orderRequestMessage);
        } catch (Exception e) {
            log.error("Failed to process order request", e);
            throw new RuntimeException("Failed to process order request", e);
        }
    }

    @KafkaListener(topics = "order-request-topic", groupId = "order-group")
    public void updateOrderStatusDelivery(String message) {
        log.info("Received delivery status update message: {}", message);
        String cleanMessage = message.replaceAll("^\"|\"$", "");
        Long orderId = Long.parseLong(cleanMessage);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.DELIVERED);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order status updated to DELIVERED for order ID: {}", orderId);
    }

    @KafkaListener(topics = "order-status-update-topic", groupId = "order-json-group")
    public void updateOrderStatus(String message) {
        log.info("Received order status update message: {}", message);
        try {
            OrderRequest orderRequest = objectMapper.readValue(message, OrderRequest.class);
            Optional<Order> orderOptional = orderRepository.findById(orderRequest.getOrderId());

            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();

                if (orderRequest.getStatus().equals("invented")) {
                    order.setStatus(OrderStatus.INVENTED);
                } else {
                    order.setStatus(OrderStatus.INVENTMENT_FAILED);
                }

                Order updatedOrder = orderRepository.save(order);
                log.info("Order status updated to {} for order ID: {}", order.getStatus(), orderRequest.getOrderId());
            } else {
                log.error("Order not found for ID: {}", orderRequest.getOrderId());
                throw new RuntimeException("Order not found for ID: " + orderRequest.getOrderId());
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid order status: {}", e.getMessage());
            throw new RuntimeException("Invalid order status: " + e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse order request: {}", e.getMessage());
            throw new RuntimeException("Failed to parse order request", e);
        } catch (Exception e) {
            log.error("Failed to update order status: {}", e.getMessage());
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    @KafkaListener(topics = "payment_success_topic", groupId = "order-group")
    public void updateOrderStatusSuccess(String message) {
        log.info("Received payment success message: {}", message);
        String cleanMessage = message.replaceAll("^\"|\"$", "");
        Long orderId = Long.parseLong(cleanMessage);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.PAID);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order status updated to PAID for order: {}", updatedOrder);
    }

    @KafkaListener(topics = "payment_failure_topic", groupId = "order-group")
    public void updateOrderStatusFailure(String message) {
        log.info("Received payment failure message: {}", message);
        String cleanMessage = message.replaceAll("^\"|\"$", "");
        Long orderId = Long.parseLong(cleanMessage);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.PAYMENT_FAILED);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order status updated to PAID for order: {}", updatedOrder);
    }

    public Order getOrderDetails(Long orderId) {
        log.info("Fetching order details for order ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order update(Long orderId, String status) {
        log.info("Updating order with ID: {} to status: {}", orderId, status);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}