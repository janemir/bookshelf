package com.example.bookshelf.controller;

import com.example.bookshelf.dto.AuthResponse;
import com.example.bookshelf.dto.LoginRequest;
import com.example.bookshelf.dto.RegisterRequest;
import com.example.bookshelf.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Аутентификация",
        description = "Регистрация, логин и подтверждение email пользователя")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Зарегистрировать пользователя",
            description = "Регистрирует нового пользователя. Отправьте данные в теле запроса в формате JSON (RegisterRequest). После регистрации отправляется письмо для подтверждения email.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
                    @ApiResponse(responseCode = "400", description = "Неверные данные регистрации")
            }
    )
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @Operation(
            summary = "Логин пользователя",
            description = "Авторизует пользователя. Отправьте данные в теле запроса в формате JSON (LoginRequest). Возвращает токен аутентификации.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный логин, возвращён токен"),
                    @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
            }
    )
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(
            summary = "Подтвердить email",
            description = "Подтверждает email пользователя по токену, полученному в письме. Укажите `token` в параметре запроса.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email успешно подтверждён"),
                    @ApiResponse(responseCode = "400", description = "Неверный или истёкший токен")
            }
    )
    @GetMapping("/verify")
    public void verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
    }
}