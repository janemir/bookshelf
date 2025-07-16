package com.example.bookshelf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Открываем доступ к endpoint регистрации и подтверждения
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/verify").permitAll()
                        // Доступ к Swagger UI и API-документации
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                // Включаем базовую HTTP-аутентификацию для тестов
                .httpBasic();

        // Отключаем CSRF для упрощения (позже включим при необходимости)
        http.csrf().disable();

        return http.build();
    }
}