package com.example.authenticationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private String role;
    private String lastName;
    private String firstName;
    private double balance;
}
