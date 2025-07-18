package com.example.bookshelf.controller;

import com.example.bookshelf.dto.BookRequest;
import com.example.bookshelf.dto.BookResponse;
import com.example.bookshelf.service.BookConversionService;
import com.example.bookshelf.service.BookService;
import com.example.bookshelf.service.BookProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Main methods")
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookConversionService bookConversionService;
    private final BookProgressService bookProgressService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadBook(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл пустой");
        }
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "books";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = uploadDir + File.separator + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            bookConversionService.convertBookToHtml(filePath);

            return ResponseEntity.ok("Файл успешно загружен: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка загрузки: " + e.getMessage());
        }
    }

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

    @GetMapping("/{id}/read")
    public ResponseEntity<?> readBook(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam(required = false) Integer page
    ) {

        String uploadDir = System.getProperty("user.dir") + File.separator + "books";
        String htmlPath = uploadDir + File.separator + id + ".html";

        int currentPage = (page != null) ? page : bookProgressService.getProgress(userId, id);

        if (page != null) {
            bookProgressService.saveOrUpdateProgress(userId, id, page);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("bookId", id);
        response.put("userId", userId);
        response.put("currentPage", currentPage);
        response.put("htmlPath", htmlPath);

        return ResponseEntity.ok(response);
    }
}