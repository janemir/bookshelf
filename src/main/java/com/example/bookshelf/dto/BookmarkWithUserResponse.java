package com.example.bookshelf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkWithUserResponse {
    private Long id;
    private Long userId;
    private String username;
    private String note;
    private Integer pageNumber;
    private LocalDateTime createdAt;
} 