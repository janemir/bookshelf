package com.example.bookshelf.repository;

import com.example.bookshelf.entity.BookProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookProgressRepository extends JpaRepository<BookProgress, Long> {
    Optional<BookProgress> findByUserIdAndBookId(Long userId, Long bookId);
}