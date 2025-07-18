package com.example.bookshelf.controller;

import com.example.bookshelf.dto.BookRequest;
import com.example.bookshelf.dto.BookResponse;
import com.example.bookshelf.service.BookConversionService;
import com.example.bookshelf.service.BookProgressService;
import com.example.bookshelf.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

@Tag(name = "Книги",
        description = "Управление книгами: загрузка, создание, обновление, удаление и чтение")
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookConversionService bookConversionService;
    private final BookProgressService bookProgressService;

    @Operation(
            summary = "Загрузить книгу",
            description = "Загружает файл книги и сохраняет его в директорию /books. Укажите файл через параметр `file`. Файл конвертируется в HTML. Возвращает имя загруженного файла.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Файл успешно загружен",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Файл пустой"),
                    @ApiResponse(responseCode = "500", description = "Ошибка при загрузке или конвертации")
            }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            summary = "Создать новую книгу",
            description = "Добавляет новую книгу в систему. Отправьте данные книги в теле запроса в формате JSON (BookRequest). Возвращает созданную книгу с присвоенным ID.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Книга успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@RequestBody BookRequest request) {
        return bookService.createBook(request);
    }

    @Operation(
            summary = "Получить все книги",
            description = "Возвращает список всех книг в системе. Если книг нет, возвращает пустой список. Отправьте GET-запрос без параметров.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список книг успешно получен")
            }
    )
    @GetMapping
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    @Operation(
            summary = "Получить книгу по ID",
            description = "Возвращает детали книги по её `id`. Укажите ID в пути. Если книга не найдена, возвращает 404.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Книга успешно найдена"),
                    @ApiResponse(responseCode = "404", description = "Книга не найдена")
            }
    )
    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @Operation(
            summary = "Обновить книгу",
            description = "Обновляет данные книги по её `id`. Отправьте обновлённые данные в теле запроса в формате JSON (BookRequest). Возвращает обновлённую книгу.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Книга успешно обновлена"),
                    @ApiResponse(responseCode = "404", description = "Книга не найдена"),
                    @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
            }
    )
    @PutMapping("/{id}")
    public BookResponse updateBook(@PathVariable Long id, @RequestBody BookRequest request) {
        return bookService.updateBook(id, request);
    }

    @Operation(
            summary = "Удалить книгу",
            description = "Удаляет книгу по её `id`. Укажите ID в пути. Возвращает статус 204 при успехе.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Книга успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Книга не найдена")
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    @Operation(
            summary = "Читать книгу",
            description = "Возвращает данные для чтения книги по её `id` для указанного `userId`. Укажите `page` (номер страницы, опционально) для перехода к конкретной странице. Сохраняет прогресс чтения. Возвращает объект с `bookId`, `userId`, `currentPage` и `htmlPath`.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные для чтения успешно получены"),
                    @ApiResponse(responseCode = "404", description = "Книга не найдена")
            }
    )
    @GetMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> readBook(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam(required = false) Integer page) {

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