// src/main/java/com/example/bookshelf/dto/AuthResponse.java
package com.example.bookshelf.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
}