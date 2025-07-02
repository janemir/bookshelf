package com.example.bookshelf.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private boolean emailVerified;
    private String message;

    public static AuthenticationResponse needsEmailVerification(String token) {
        return new AuthenticationResponse(token, false, "Проверьте email для подтверждения регистрации");
    }
}
