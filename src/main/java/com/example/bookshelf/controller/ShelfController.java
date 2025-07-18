package com.example.bookshelf.controller;

import com.example.bookshelf.entity.Shelf;
import com.example.bookshelf.service.ShelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(
        name = "Полки",
        description = "Управление книжными полками пользователя: создание, добавление/удаление книг, просмотр"
)
@RestController
@RequestMapping("/api/v1/shelves")
@RequiredArgsConstructor
public class ShelfController {
    private final ShelfService shelfService;

    @Operation(
            summary = "Создать новую полку",
            description = "Создаёт новую книжную полку для пользователя. Укажите `userId` (ID пользователя) и `name` (название полки) в параметрах запроса. Возвращает созданную полку.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Полка успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры или пользователь не найден")
            }
    )
    @PostMapping("/create")
    public ResponseEntity<Shelf> createShelf(@RequestParam Long userId, @RequestParam String name) {
        Shelf shelf = shelfService.createShelf(userId, name);
        return ResponseEntity.ok(shelf);
    }

    @Operation(
            summary = "Добавить книгу на полку",
            description = "Добавляет книгу на полку пользователя. Укажите `shelfId` (ID полки) в пути и `bookId` (ID книги) в параметре запроса. Возвращает обновлённую полку или ошибку, если полка/книга не найдены.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Книга успешно добавлена"),
                    @ApiResponse(responseCode = "400", description = "Полка или книга не найдены")
            }
    )
    @PostMapping("/{shelfId}/add-book")
    public ResponseEntity<Shelf> addBookToShelf(@PathVariable Long shelfId, @RequestParam Long bookId) {
        Optional<Shelf> shelf = shelfService.addBookToShelf(shelfId, bookId);
        return shelf.<ResponseEntity<Shelf>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body(null));
    }

    @Operation(
            summary = "Удалить книгу с полки",
            description = "Удаляет книгу с полки пользователя. Укажите `shelfId` (ID полки) и `bookId` (ID книги) в пути. Возвращает обновлённую полку или ошибку, если полка/книга не найдены.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Книга успешно удалена"),
                    @ApiResponse(responseCode = "400", description = "Полка или книга не найдены")
            }
    )
    @DeleteMapping("/{shelfId}/book/{bookId}")
    public ResponseEntity<Shelf> removeBookFromShelf(@PathVariable Long shelfId, @PathVariable Long bookId) {
        Optional<Shelf> shelf = shelfService.removeBookFromShelf(shelfId, bookId);
        return shelf.<ResponseEntity<Shelf>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body(null));
    }

    @Operation(
            summary = "Получить все полки пользователя",
            description = "Возвращает список всех полок пользователя по его `userId`. Включает содержимое (книги) каждой полки. Если полок нет, возвращает пустой список.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список полок успешно получен")
            }
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Shelf>> getShelvesByUser(@PathVariable Long userId) {
        List<Shelf> shelves = shelfService.getShelvesByUserId(userId);
        return ResponseEntity.ok(shelves);
    }
}