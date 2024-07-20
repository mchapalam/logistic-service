package com.example.authenticationservice.service.impl;

import com.example.authenticationservice.dto.TokenResponse;
import com.example.authenticationservice.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final WebClient webClient;

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;


    public Mono<String> getAccessToken(String username, String password) {
        log.info("Requesting access token for realm: {}", realm);
        log.info("Using client ID: {}", clientId);

        return webClient.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&grant_type=password" +
                        "&username=" + username +
                        "&password=" + password)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::getAccessToken)
                .doOnSuccess(token -> log.info("Successfully obtained access token"))
                .doOnError(error -> log.error("Failed to obtain access token", error));
    }

    public Mono<Void> createUser(UserRequest userRequestDTO) {
        return getAccessToken(userRequestDTO.getUsername(), userRequestDTO.getPassword())
                .flatMap(accessToken -> {
                    UserRepresentation user = new UserRepresentation();
                    user.setUsername(userRequestDTO.getUsername());
                    user.setEnabled(true);

                    CredentialRepresentation credential = new CredentialRepresentation();
                    credential.setType(CredentialRepresentation.PASSWORD);
                    credential.setValue(userRequestDTO.getPassword());
                    user.setCredentials(Arrays.asList(credential));

                    return webClient.post()
                            .uri("/admin/realms/master/users")
                            .header("Authorization", "Bearer " + accessToken)
                            .bodyValue(user)
                            .retrieve()
                            .toBodilessEntity()
                            .doOnError(error -> {
                                throw new RuntimeException(error.getMessage());
                            })
                            .then();
                });
    }

}