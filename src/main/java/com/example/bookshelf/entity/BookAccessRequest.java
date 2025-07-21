package com.example.bookshelf.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "book_access_request")
public class BookAccessRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromUserId;
    private Long toUserId;
    private Long bookId;

    private String comment;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime expiresAt;
    private String reason;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }
} 