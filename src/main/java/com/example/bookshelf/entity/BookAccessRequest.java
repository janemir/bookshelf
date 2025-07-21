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

    private Long fromUserId; // кто запрашивает
    private Long toUserId;   // владелец книги
    private Long bookId;

    private String comment;  // комментарий к запросу

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime expiresAt; // до какого времени доступ
    private String reason; // причина отказа/комментарий владельца

    public enum Status {
        PENDING, APPROVED, REJECTED
    }
} 