package com.example.authenticationservice.service;

import com.example.authenticationservice.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(Long id);

    User findByUsername(String username);

    User create(User user);

    User update(Long id, User user);

    void delete(Long id);
}
