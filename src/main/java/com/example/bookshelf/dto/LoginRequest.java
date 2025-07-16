// src/main/java/com/example/bookshelf/dto/LoginRequest.java
package com.example.bookshelf.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}