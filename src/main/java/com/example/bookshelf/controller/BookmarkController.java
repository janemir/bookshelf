package com.example.bookshelf.controller;

import com.example.bookshelf.entity.Bookmark;
import com.example.bookshelf.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.bookshelf.entity.BookPage;
import com.example.bookshelf.repository.BookPageRepository;
import com.example.bookshelf.entity.BookEntity;
import com.example.bookshelf.repository.BookRepository;
import com.example.bookshelf.service.BookAccessRequestService;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/bookmarks")
@Tag(name = "Закладки", description = "Управление закладками пользователя в книгах")
public class BookmarkController {
    private final BookmarkService bookmarkService;
    private final BookPageRepository bookPageRepository;
    private final BookRepository bookRepository;
    private final BookAccessRequestService bookAccessRequestService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService, BookPageRepository bookPageRepository, BookRepository bookRepository, BookAccessRequestService bookAccessRequestService) {
        this.bookmarkService = bookmarkService;
        this.bookPageRepository = bookPageRepository;
        this.bookRepository = bookRepository;
        this.bookAccessRequestService = bookAccessRequestService;
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

    @Operation(summary = "Перейти по закладке", description = "Открыть страницу книги по id закладки (с проверкой доступа)")
    @GetMapping("/goto/{bookmarkId}")
    public ResponseEntity<?> gotoBookmark(@PathVariable Long bookmarkId, @RequestParam Long userId) {
        Bookmark bookmark = bookmarkService.getAllBookmarks(userId).stream()
            .filter(b -> b.getId().equals(bookmarkId)).findFirst().orElse(null);
        if (bookmark == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Закладка не найдена");
        }
        BookEntity book = bookRepository.findById(bookmark.getBookId()).orElse(null);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Книга не найдена");
        }
        if (!userId.equals(book.getUserId()) && !bookAccessRequestService.hasActiveAccess(userId, book.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Нет доступа к книге");
        }
        Optional<BookPage> pageOpt = bookPageRepository.findByBookIdAndPageNumber(book.getId(), bookmark.getPageNumber());
        if (pageOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Страница не найдена");
        }
        BookPage page = pageOpt.get();
        Map<String, Object> resp = Map.of(
            "bookId", book.getId(),
            "userId", userId,
            "pageNumber", bookmark.getPageNumber(),
            "content", page.getContent(),
            "bookmarkId", bookmarkId,
            "note", bookmark.getNote()
        );
        return ResponseEntity.ok(resp);
    }
} 