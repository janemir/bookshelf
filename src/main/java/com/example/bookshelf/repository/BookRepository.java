package com.example.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.bookshelf.entity.BookEntity;

public interface BookRepository extends JpaRepository<BookEntity, Long>{
}
