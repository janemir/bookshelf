package com.example.bookshelf.service;

import com.example.bookshelf.dto.AuthResponse;
import com.example.bookshelf.dto.LoginRequest;
import com.example.bookshelf.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void verifyEmail(String token);
}