package com.example.authenticationservice.controller;

import com.example.authenticationservice.dto.ResponseMessage;
import com.example.authenticationservice.dto.TokenResponse;
import com.example.authenticationservice.dto.UserRequest;
import com.example.authenticationservice.model.User;
import com.example.authenticationservice.service.UserService;
import com.example.authenticationservice.service.impl.AuthServiceImpl;
import com.example.authenticationservice.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthServiceImpl authService;
    private final UserService userService;

    @PostMapping("/token")
    public Mono<ResponseEntity<TokenResponse>> getToken(
            @RequestParam String username,
            @RequestParam String password) {
        return authService.getAccessToken(username, password)
                .map(token -> ResponseEntity.ok(new TokenResponse(token)))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null)));
    }


    @PostMapping("/create")
    public Mono<ResponseEntity<ResponseMessage>> createUser(@RequestBody UserRequest userRequestDTO) {
        User user = new User();

        user.setBalance(userRequestDTO.getBalance());
        user.setUsername(userRequestDTO.getUsername());

        userService.create(user);

        return authService.createUser(userRequestDTO)
                .then(Mono.just(ResponseEntity.ok(new ResponseMessage("User created successfully"))));
    }
}