package com.example.bookshelf.controller;

import com.example.bookshelf.service.auth.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final EmailVerificationService verificationService;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        verificationService.verifyEmail(token);
        return ResponseEntity.ok("Email успешно подтвержден!");
    }
}
