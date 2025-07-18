package com.example.bookshelf.service;

import com.example.bookshelf.entity.BookProgress;
import com.example.bookshelf.repository.BookProgressRepository;
import com.example.bookshelf.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookProgressService {
    private final BookProgressRepository bookProgressRepository;
    private final BookRepository bookRepository;

    public BookProgress saveOrUpdateProgress(Long userId, Long bookId, Integer pageNumber) {

        if (!bookRepository.existsById(bookId)) {
            throw new RuntimeException("Книга с id " + bookId + " не найдена");
        }

        return bookProgressRepository.findByUserIdAndBookId(userId, bookId)
                .map(progress -> {
                    progress.setPageNumber(pageNumber);
                    return bookProgressRepository.save(progress);
                })
                .orElseGet(() -> bookProgressRepository.save(
                        BookProgress.builder()
                                .userId(userId)
                                .bookId(bookId)
                                .pageNumber(pageNumber)
                                .build()
                ));
    }

    public Integer getProgress(Long userId, Long bookId) {
        return bookProgressRepository.findByUserIdAndBookId(userId, bookId)
                .map(BookProgress::getPageNumber)
                .orElse(1); // по умолчанию первая страница
    }
}