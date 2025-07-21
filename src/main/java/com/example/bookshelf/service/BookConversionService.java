package com.example.bookshelf.service;

import com.example.bookshelf.entity.BookEntity;
import com.example.bookshelf.entity.BookPage;
import com.example.bookshelf.repository.BookPageRepository;
import com.example.bookshelf.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.*;

@Service
@RequiredArgsConstructor
public class BookConversionService {
    private final BookRepository bookRepository;
    private final BookPageRepository bookPageRepository;

    @Async
    public void convertBookToHtml(String filePath, Long bookId) {
        File inputFile = new File(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String text = sb.toString();
            int pageSize = 2000;
            int totalPages = (int) Math.ceil((double) text.length() / pageSize);
            for (int i = 0; i < totalPages; i++) {
                int start = i * pageSize;
                int end = Math.min(start + pageSize, text.length());
                String pageContent = text.substring(start, end);
                String html = "<html><body><pre>" + pageContent + "</pre></body></html>";
                BookPage page = BookPage.builder()
                        .bookId(bookId)
                        .pageNumber(i + 1)
                        .content(html)
                        .build();
                bookPageRepository.save(page);
            }
            bookRepository.findById(bookId).ifPresent(book -> {
                book.setConverted(true);
                bookRepository.save(book);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}