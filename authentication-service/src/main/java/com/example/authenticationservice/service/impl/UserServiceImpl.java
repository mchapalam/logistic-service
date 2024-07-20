package com.example.authenticationservice.service.impl;

import com.example.authenticationservice.model.User;
import com.example.authenticationservice.repository.UserRepository;
import com.example.authenticationservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.jose.jwk.JWK;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User user) {
        User existingUser = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found"));
        existingUser.setUsername(user.getUsername());
        existingUser.setBalance(user.getBalance());

        return userRepository.save(existingUser);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id).get();

        userRepository.delete(user);
    }
}
