// src/main/java/com/example/bookshelf/controller/AuthController.java
package com.example.bookshelf.controller;

import com.example.bookshelf.dto.AuthResponse;
import com.example.bookshelf.dto.LoginRequest;
import com.example.bookshelf.dto.RegisterRequest;
import com.example.bookshelf.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Регистрирует нового пользователя")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @Operation(summary = "Логинит пользователя")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Проверяет email")
    @GetMapping("/verify")
    public void verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
    }
}