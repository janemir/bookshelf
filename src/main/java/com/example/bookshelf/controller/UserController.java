package com.example.bookshelf.controller;

import com.example.bookshelf.entity.UserEntity;
import com.example.bookshelf.entity.Shelf;
import com.example.bookshelf.entity.BookEntity;
import com.example.bookshelf.repository.UserRepository;
import com.example.bookshelf.repository.ShelfRepository;
import com.example.bookshelf.repository.BookRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Публичный профиль", description = "Публичная информация о пользователе: имя, полки, публичные книги")
public class UserController {
    private final UserRepository userRepository;
    private final ShelfRepository shelfRepository;
    private final BookRepository bookRepository;

    @Autowired
    public UserController(UserRepository userRepository, ShelfRepository shelfRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.shelfRepository = shelfRepository;
        this.bookRepository = bookRepository;
    }

    @Operation(summary = "Публичный профиль пользователя", description = "Получить публичную информацию о пользователе: имя, список полок, публичные книги.")
    @GetMapping("/{userId}/public-profile")
    public ResponseEntity<?> getPublicProfile(@PathVariable Long userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<Shelf> shelves = shelfRepository.findByUserId(userId);
        List<BookEntity> books = bookRepository.findAll().stream()
            .filter(b -> userId.equals(b.getUserId()))
            .toList();
        Map<String, Object> resp = Map.of(
            "userId", user.getId(),
            "username", user.getUsername(),
            "shelves", shelves,
            "books", books
        );
        return ResponseEntity.ok(resp);
    }
} 