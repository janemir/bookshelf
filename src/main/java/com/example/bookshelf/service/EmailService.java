// src/main/java/com/example/bookshelf/service/EmailService.java
package com.example.bookshelf.service;

public interface EmailService {
    void sendVerificationEmail(String email, String token);
}