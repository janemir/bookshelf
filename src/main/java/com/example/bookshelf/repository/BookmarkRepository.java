package com.example.bookshelf.repository;

import com.example.bookshelf.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);
    List<Bookmark> findByUserIdAndBookId(Long userId, Long bookId);
    List<Bookmark> findByBookId(Long bookId);
} 