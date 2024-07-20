package com.example.payment.service.model;

import lombok.Data;

@Data
public class User {
    private Long id;

    private String username;
    private double balance;
}
