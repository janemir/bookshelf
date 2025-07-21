package com.example.bookshelf.controller;

import com.example.bookshelf.dto.BookRequest;
import com.example.bookshelf.dto.BookResponse;
import com.example.bookshelf.dto.BookUploadRequest;
import com.example.bookshelf.service.BookConversionService;
import com.example.bookshelf.service.BookProgressService;
import com.example.bookshelf.service.BookService;
import com.example.bookshelf.service.BookAccessRequestService;
import com.example.bookshelf.entity.BookPage;
import com.example.bookshelf.entity.BookEntity;
import com.example.bookshelf.repository.BookPageRepository;
import com.example.bookshelf.repository.BookRepository;
import com.example.bookshelf.dto.BookDetailsResponse;
import com.example.bookshelf.entity.Shelf;
import com.example.bookshelf.repository.ShelfRepository;
import com.example.bookshelf.entity.BookAccessRequest;
import com.example.bookshelf.repository.BookAccessRequestRepository;
import com.example.bookshelf.entity.UserEntity;
import com.example.bookshelf.repository.UserRepository;
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
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Tag(name = "Книги",
        description = "Управление книгами: загрузка, создание, обновление, удаление и чтение.")
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookConversionService bookConversionService;
    private final BookProgressService bookProgressService;
    private final BookPageRepository bookPageRepository;
    private final BookAccessRequestService bookAccessRequestService;
    private final BookRepository bookRepository;
    private final ShelfRepository shelfRepository;
    private final BookAccessRequestRepository accessRequestRepository;
    private final UserRepository userRepository;

    @Operation(
            summary = "Загрузить книгу (только файл)",
            description = "Загружает файл книги и сохраняет его в директорию /books. Возвращает bookId для последующего обновления метаданных.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Файл успешно загружен и создана книга (bookId)",
                            content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "400", description = "Файл пустой"),
                    @ApiResponse(responseCode = "500", description = "Ошибка при загрузке или конвертации")
            }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadBook(@RequestParam("file") MultipartFile file, @RequestParam Long userId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "books";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = uploadDir + File.separator + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            BookRequest bookRequest = new BookRequest();
            bookRequest.setTitle(file.getOriginalFilename());
            bookRequest.setUserId(userId);
            BookResponse bookResponse = bookService.createBook(bookRequest);

            bookConversionService.convertBookToHtml(filePath, bookResponse.getId());

            return ResponseEntity.ok(bookResponse.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
            description = "Возвращает данные для чтения книги по её `id` для указанного `userId`. Укажите `page` (номер страницы, опционально) для перехода к конкретной странице. Сохраняет прогресс чтения. Возвращает объект с `bookId`, `userId`, `currentPage`, `totalPages`, `content`.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные для чтения успешно получены"),
                    @ApiResponse(responseCode = "404", description = "Книга не найдена")
            }
    )
    @GetMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> readBook(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam(required = false) Integer page
    ) {
        BookEntity book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Книга не найдена"));
        }
        if (!userId.equals(book.getUserId()) && !bookAccessRequestService.hasActiveAccess(userId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "У вас нет доступа к этой книге. Запросите доступ у владельца."));
        }
        int totalPages = bookPageRepository.countByBookId(id);
        if (totalPages == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Книга не найдена или не сконвертирована"));
        }
        int currentPage = (page != null && page > 0 && page <= totalPages)
                ? page
                : bookProgressService.getProgress(userId, id);
        if (currentPage > totalPages) currentPage = totalPages;
        bookProgressService.saveOrUpdateProgress(userId, id, currentPage);
        BookPage bookPage = bookPageRepository.findByBookIdAndPageNumber(id, currentPage).orElse(null);
        if (bookPage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Страница не найдена"));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("bookId", id);
        response.put("userId", userId);
        response.put("currentPage", currentPage);
        response.put("totalPages", totalPages);
        response.put("content", bookPage.getContent());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Получить подробную информацию о книге",
        description = "Возвращает подробную информацию о книге: владелец, пользователи с доступом, полки."
    )
    @GetMapping("/{id}/details")
    public ResponseEntity<BookDetailsResponse> getBookDetails(@PathVariable Long id) {
        BookEntity book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        UserEntity owner = userRepository.findById(book.getUserId()).orElse(null);
        BookDetailsResponse.OwnerInfo ownerInfo = owner == null ? null :
            new BookDetailsResponse.OwnerInfo(owner.getId(), owner.getUsername(), owner.getEmail());

        List<BookAccessRequest> accessRequests = accessRequestRepository.findByBookIdAndStatus(id, BookAccessRequest.Status.APPROVED);
        List<BookDetailsResponse.UserInfo> accessUsers = accessRequests.stream()
            .filter(req -> req.getExpiresAt() == null || req.getExpiresAt().isAfter(LocalDateTime.now()))
            .map(req -> {
                UserEntity u = userRepository.findById(req.getFromUserId()).orElse(null);
                return u == null ? null : new BookDetailsResponse.UserInfo(u.getId(), u.getUsername(), u.getEmail());
            })
            .filter(u -> u != null)
            .collect(Collectors.toList());

        List<Shelf> shelves = shelfRepository.findByBooks_Id(id);
        List<BookDetailsResponse.ShelfInfo> shelfInfos = shelves.stream()
            .map(shelf -> new BookDetailsResponse.ShelfInfo(shelf.getId(), shelf.getName()))
            .collect(Collectors.toList());

        BookDetailsResponse resp = new BookDetailsResponse();
        resp.setId(book.getId());
        resp.setTitle(book.getTitle());
        resp.setAuthor(book.getAuthor());
        resp.setDescription(book.getDescription());
        resp.setOwner(ownerInfo);
        resp.setAccessUsers(accessUsers);
        resp.setShelves(shelfInfos);
        return ResponseEntity.ok(resp);
    }
}