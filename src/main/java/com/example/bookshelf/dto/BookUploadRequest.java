package com.example.bookshelf.dto;

import lombok.Data;

@Data
public class BookUploadRequest {
    private String title;
    private String author;
    private String description;
} 