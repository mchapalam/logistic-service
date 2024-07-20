package com.example.gatewayapi.configuration;

import com.example.gatewayapi.configuration.util.CustomJwtConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomJwtConverter customJwtConverter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        return serverHttpSecurity
                .authorizeExchange(exchange -> exchange
                        .matchers(ServerWebExchangeMatchers.pathMatchers("/eureka/**")).permitAll()
                        .matchers(ServerWebExchangeMatchers.pathMatchers("/*/api/inventory/all")).permitAll()
                        .matchers(ServerWebExchangeMatchers.pathMatchers("/*/*/*/public")).permitAll()
                        .matchers(ServerWebExchangeMatchers.pathMatchers("/*/*/*/public")).permitAll()
                        .matchers(ServerWebExchangeMatchers.pathMatchers("/*/*/auth/**")).permitAll()
                        .matchers(ServerWebExchangeMatchers.pathMatchers("/*/*/*/username/*")).permitAll()
                        .matchers(ServerWebExchangeMatchers.pathMatchers("/admin/**")).hasRole("admin")
                        .matchers(ServerWebExchangeMatchers.pathMatchers("/moderator/**")).hasRole("moderator")
                        .matchers(ServerWebExchangeMatchers.pathMatchers("/metrics")).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter(){
        var jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                new ReactiveJwtGrantedAuthoritiesConverterAdapter(customJwtConverter)
        );

        return jwtAuthenticationConverter;
    }
}
