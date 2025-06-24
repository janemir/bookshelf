package com.example.bookshelf.service;

import com.example.bookshelf.dto.BookRequest;
import com.example.bookshelf.dto.BookResponse;
import java.util.List;

public interface BookService {
    BookResponse createBook(BookRequest request);
    List<BookResponse> getAllBooks();
    BookResponse getBookById(Long id);
    BookResponse updateBook(Long id, BookRequest request);
    void deleteBook(Long id);
}
