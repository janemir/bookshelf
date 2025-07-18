package com.example.bookshelf.service;

import com.example.bookshelf.entity.BookEntity;
import com.example.bookshelf.entity.Shelf;
import com.example.bookshelf.repository.BookRepository;
import com.example.bookshelf.repository.ShelfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShelfService {
    private final ShelfRepository shelfRepository;
    private final BookRepository bookRepository;

    public Shelf createShelf(Long userId, String name) {
        Shelf shelf = Shelf.builder()
                .userId(userId)
                .name(name)
                .build();
        return shelfRepository.save(shelf);
    }

    public Optional<Shelf> addBookToShelf(Long shelfId, Long bookId) {
        Optional<Shelf> shelfOpt = shelfRepository.findById(shelfId);
        Optional<BookEntity> bookOpt = bookRepository.findById(bookId);
        if (shelfOpt.isPresent() && bookOpt.isPresent()) {
            Shelf shelf = shelfOpt.get();
            shelf.getBooks().add(bookOpt.get());
            return Optional.of(shelfRepository.save(shelf));
        }
        return Optional.empty();
    }

    public Optional<Shelf> removeBookFromShelf(Long shelfId, Long bookId) {
        Optional<Shelf> shelfOpt = shelfRepository.findById(shelfId);
        Optional<BookEntity> bookOpt = bookRepository.findById(bookId);
        if (shelfOpt.isPresent() && bookOpt.isPresent()) {
            Shelf shelf = shelfOpt.get();
            shelf.getBooks().remove(bookOpt.get());
            return Optional.of(shelfRepository.save(shelf));
        }
        return Optional.empty();
    }

    public List<Shelf> getShelvesByUserId(Long userId) {
        return shelfRepository.findByUserId(userId);
    }
} 