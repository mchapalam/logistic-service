package com.example.authenticationservice.controller;

import com.example.authenticationservice.model.User;
import com.example.authenticationservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id){
        return userService.findById(id);
    }

    @GetMapping("/username/{username}")
    public User findByUsername(@PathVariable String username){
        return userService.findByUsername(username);
    }
}
