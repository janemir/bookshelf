package com.example.bookshelf.repository;

import com.example.bookshelf.entity.BookPage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookPageRepository extends JpaRepository<BookPage, Long> {
    Optional<BookPage> findByBookIdAndPageNumber(Long bookId, Integer pageNumber);
    List<BookPage> findByBookIdOrderByPageNumber(Long bookId);
    int countByBookId(Long bookId);
} 