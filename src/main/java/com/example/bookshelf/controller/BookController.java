package com.example.bookshelf.controller;

import com.example.bookshelf.dto.BookRequest;
import com.example.bookshelf.dto.BookResponse;
import com.example.bookshelf.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Main methods")
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "Создает новую книгу",
            description = "Добавляет новую книгу в систему. Возвращает созданную книгу с присвоенным ID"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@RequestBody BookRequest request) {
        return bookService.createBook(request);
    }

    @Operation(
            summary = "Получает список всех книг",
            description = "Возвращает полный перечень книг, имеющихся в системе. Список может быть пустым"
    )
    @GetMapping
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    @Operation(
            summary = "Находит книгу по ID",
            description = "Возвращает детали книги по ID. Если книга не найдена, возвращает 404"
    )
    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @Operation(
            summary = "Обновляет данные книги",
            description = "Обновляет информацию о книге с указанным ID. Возвращает обновлённые данные. Если книга не найдена, возвращает 404"
    )
    @PutMapping("/{id}")
    public BookResponse updateBook(
            @PathVariable Long id,
            @RequestBody BookRequest request
    ) {
        return bookService.updateBook(id, request);
    }

    @Operation(
            summary = "Удаляет книгу",
            description = "Удаляет книгу из системы по её ID. Возвращает статус 204 (No Content) при успешном удалении"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}