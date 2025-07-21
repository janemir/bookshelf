package com.example.bookshelf.controller;

import com.example.bookshelf.entity.Bookmark;
import com.example.bookshelf.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookmarks")
@Tag(name = "Закладки", description = "Управление закладками пользователя в книгах")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @Operation(summary = "Добавить закладку", description = "Добавляет закладку на страницу книги с заметкой.")
    @PostMapping("/add")
    public ResponseEntity<Bookmark> addBookmark(@RequestParam Long userId,
                                                @RequestParam Long bookId,
                                                @RequestParam Integer pageNumber,
                                                @RequestParam(required = false) String note) {
        Bookmark bookmark = bookmarkService.addBookmark(userId, bookId, pageNumber, note);
        return ResponseEntity.ok(bookmark);
    }

    @Operation(summary = "Удалить закладку", description = "Удаляет закладку по её id (только владелец может удалить свою закладку).")
    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long bookmarkId, @RequestParam Long userId) {
        bookmarkService.deleteBookmark(bookmarkId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить все закладки пользователя по книге", description = "Возвращает список всех закладок пользователя для выбранной книги.")
    @GetMapping("/by-book")
    public ResponseEntity<List<Bookmark>> getBookmarks(@RequestParam Long userId, @RequestParam Long bookId) {
        List<Bookmark> bookmarks = bookmarkService.getBookmarks(userId, bookId);
        return ResponseEntity.ok(bookmarks);
    }

    @Operation(summary = "Получить все закладки пользователя", description = "Возвращает список всех закладок пользователя по всем книгам.")
    @GetMapping("/by-user")
    public ResponseEntity<List<Bookmark>> getAllBookmarks(@RequestParam Long userId) {
        List<Bookmark> bookmarks = bookmarkService.getAllBookmarks(userId);
        return ResponseEntity.ok(bookmarks);
    }
} 