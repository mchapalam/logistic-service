package com.example.payment.service.service;

import com.example.payment.service.dto.OrderRequest;
import com.example.payment.service.model.*;
import com.example.payment.service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;

    private static final int MAX_RETRIES = 3;

    public Payment processPayment(Long orderId, BigDecimal amount, String username) {
        Payment payment = new Payment();

        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUsername(username);

        boolean hasSufficientFunds = checkCustomerFunds(username, amount);

        if (hasSufficientFunds) {
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            kafkaTemplate.send("payment_success_topic", orderId.toString());
            log.info("Payment processed successfully for Order ID: {}", orderId);
        } else {
            payment.setStatus(PaymentStatus.FAILURE);
            paymentRepository.save(payment);
            kafkaTemplate.send("payment_failure_topic", orderId.toString());
            log.warn("Insufficient funds for Order ID: {}. Retrying payment.", orderId);

            retryPayment(orderId, amount, username, 0);
        }

        return payment;
    }

    private void retryPayment(Long orderId, BigDecimal amount, String username, int attempt) {
        if (attempt < MAX_RETRIES) {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted during sleep before retrying payment for Order ID: {}", orderId, e);
            }
            log.info("Retrying payment for Order ID: {}. Attempt: {}", orderId, attempt + 1);

            boolean hasSufficientFunds = checkCustomerFunds(username, amount);
            if (hasSufficientFunds) {
                processPayment(orderId, amount, username);
            } else {
                retryPayment(orderId, amount, username, attempt + 1);
            }
        } else {
            log.error("Max retry attempts reached for Order ID: {}", orderId);
        }
    }

    @KafkaListener(topics = "order-topic", groupId = "payment-group", containerFactory = "kafkaListenerContainerFactory")
    public void processOrder(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();
        BigDecimal amount = orderRequest.getTotalPrice();

        log.info("Received order details: Order ID: {}, Total Amount: {}, Username: {}, Product IDs: {}, Quantities: {}",
                orderId, amount, orderRequest.getUsername(), orderRequest.getProductIds(), orderRequest.getQuantities());

        processPayment(orderId, amount, orderRequest.getUsername());
    }

    public boolean checkCustomerFunds(String username, BigDecimal amount) {
        String url = "http://logistic-gateway:8083/logistic-auth-service/api/users/username/" + username;

        try {
            User user = restTemplate.getForObject(url, User.class);
            if (user != null) {
                boolean hasFunds = BigDecimal.valueOf(user.getBalance()).compareTo(amount) >= 0;
                log.info("Checked customer funds for Username: {}. Sufficient Funds: {}", username, hasFunds);
                return hasFunds;
            }
        } catch (RestClientException e) {
            log.error("Error fetching user details for Username: {}. Error: {}", username, e.getMessage(), e);
        }

        return false;
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Payment create(Long orderId, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);
        log.info("Created new payment for Order ID: {} with Amount: {}", orderId, amount);

        return payment;
    }

}
