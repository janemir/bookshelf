package com.example.bookshelf.repository;

import com.example.bookshelf.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    List<Shelf> findByUserId(Long userId);
} 