package com.example.bookshelf.service;

public interface EmailService {
    void sendVerificationEmail(String email, String token);
}