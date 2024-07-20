package com.example.payment.service.controller;

import com.example.payment.service.model.Payment;
import com.example.payment.service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Payment processPayment(@RequestParam Long orderId, @RequestParam BigDecimal amount) {
        Payment payment = paymentService.create(orderId, amount);
        return payment;
    }

    @GetMapping
    public List<Payment> getAllPayment(){
        return paymentService.getAll();
    }
}