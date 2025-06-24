package com.example.bookshelf.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BookRequest {
    private String title;
    private String author;
    private String description;
}
