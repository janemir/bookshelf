// src/main/java/com/example/bookshelf/dto/RegisterRequest.java
package com.example.bookshelf.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String recaptchaResponse;
}