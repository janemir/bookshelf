package com.example.bookshelf.service;

import com.example.bookshelf.entity.Bookmark;
import com.example.bookshelf.repository.BookmarkRepository;
import com.example.bookshelf.repository.BookRepository;
import com.example.bookshelf.service.BookAccessRequestService;
import com.example.bookshelf.entity.BookEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final BookRepository bookRepository;
    private final BookAccessRequestService bookAccessRequestService;

    @Autowired
    public BookmarkService(BookmarkRepository bookmarkRepository, BookRepository bookRepository, BookAccessRequestService bookAccessRequestService) {
        this.bookmarkRepository = bookmarkRepository;
        this.bookRepository = bookRepository;
        this.bookAccessRequestService = bookAccessRequestService;
    }

    public Bookmark addBookmark(Long userId, Long bookId, Integer pageNumber, String note) {
        BookEntity book = bookRepository.findById(bookId).orElse(null);
        if (book == null) throw new RuntimeException("Книга не найдена");
        if (!userId.equals(book.getUserId()) && !bookAccessRequestService.hasActiveAccess(userId, bookId)) {
            throw new RuntimeException("Нет доступа к книге для добавления закладки");
        }
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setBookId(bookId);
        bookmark.setPageNumber(pageNumber);
        bookmark.setNote(note);
        bookmark.setCreatedAt(LocalDateTime.now());
        return bookmarkRepository.save(bookmark);
    }

    public void deleteBookmark(Long bookmarkId, Long userId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElse(null);
        if (bookmark != null && bookmark.getUserId().equals(userId)) {
            bookmarkRepository.deleteById(bookmarkId);
        }
    }

    public List<Bookmark> getBookmarks(Long userId, Long bookId) {
        return bookmarkRepository.findByUserIdAndBookId(userId, bookId);
    }

    public List<Bookmark> getAllBookmarks(Long userId) {
        return bookmarkRepository.findByUserId(userId);
    }

    public List<Bookmark> getAllBookmarksByBook(Long bookId) {
        return bookmarkRepository.findByBookId(bookId);
    }
} 