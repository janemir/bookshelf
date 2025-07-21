package com.example.bookshelf.controller;

import com.example.bookshelf.entity.BookAccessRequest;
import com.example.bookshelf.service.BookAccessRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Tag(name = "Доступ к книгам", description = "Запросы доступа к книгам, одобрение, отклонение, просмотр запросов.")
@RestController
@RequestMapping("/api/v1/access")
@RequiredArgsConstructor
public class BookAccessRequestController {
    private final BookAccessRequestService service;

    @Operation(
        summary = "Отправить запрос на доступ к книге",
        description = "Пользователь отправляет запрос владельцу книги на доступ к чтению. Можно добавить комментарий."
    )
    @PostMapping("/request")
    public ResponseEntity<BookAccessRequest> sendRequest(
            @RequestParam Long fromUserId,
            @RequestParam Long toUserId,
            @RequestParam Long bookId,
            @RequestParam(required = false) String comment
    ) {
        BookAccessRequest req = service.sendRequest(fromUserId, toUserId, bookId, comment);
        return ResponseEntity.ok(req);
    }

    @Operation(
        summary = "Входящие запросы на доступ",
        description = "Получить список всех входящих запросов на доступ к книгам для владельца."
    )
    @GetMapping("/incoming")
    public List<BookAccessRequest> getIncoming(@RequestParam Long toUserId) {
        return service.getIncoming(toUserId);
    }

    @Operation(
        summary = "Исходящие запросы на доступ",
        description = "Получить список всех исходящих запросов пользователя на доступ к чужим книгам."
    )
    @GetMapping("/outgoing")
    public List<BookAccessRequest> getOutgoing(@RequestParam Long fromUserId) {
        return service.getOutgoing(fromUserId);
    }

    @Operation(
        summary = "Одобрить запрос на доступ",
        description = "Владелец книги одобряет запрос на доступ, может указать срок действия (expiresAt) и комментарий."
    )
    @PostMapping("/approve")
    public ResponseEntity<BookAccessRequest> approveRequest(
            @RequestParam Long requestId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiresAt,
            @RequestParam(required = false) String comment
    ) {
        Optional<BookAccessRequest> req = service.approveRequest(requestId, expiresAt, comment);
        return req.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Отклонить запрос на доступ",
        description = "Владелец книги отклоняет запрос на доступ, указывает причину отказа."
    )
    @PostMapping("/reject")
    public ResponseEntity<BookAccessRequest> rejectRequest(
            @RequestParam Long requestId,
            @RequestParam String reason
    ) {
        Optional<BookAccessRequest> req = service.rejectRequest(requestId, reason);
        return req.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
} 