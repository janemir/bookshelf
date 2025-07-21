package com.example.bookshelf.repository;

import com.example.bookshelf.entity.BookAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookAccessRequestRepository extends JpaRepository<BookAccessRequest, Long> {
    List<BookAccessRequest> findByToUserId(Long toUserId);
    List<BookAccessRequest> findByFromUserId(Long fromUserId);
    List<BookAccessRequest> findByBookIdAndToUserId(Long bookId, Long toUserId);
    List<BookAccessRequest> findByBookIdAndFromUserId(Long bookId, Long fromUserId);
    List<BookAccessRequest> findByBookIdAndStatus(Long bookId, BookAccessRequest.Status status);
} 