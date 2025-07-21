package com.example.bookshelf.service;

import com.example.bookshelf.entity.BookAccessRequest;
import com.example.bookshelf.repository.BookAccessRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookAccessRequestService {
    private final BookAccessRequestRepository repository;

    public BookAccessRequest sendRequest(Long fromUserId, Long toUserId, Long bookId, String comment) {
        BookAccessRequest request = BookAccessRequest.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .bookId(bookId)
                .comment(comment)
                .status(BookAccessRequest.Status.PENDING)
                .build();
        return repository.save(request);
    }

    public List<BookAccessRequest> getIncoming(Long toUserId) {
        return repository.findByToUserId(toUserId);
    }

    public List<BookAccessRequest> getOutgoing(Long fromUserId) {
        return repository.findByFromUserId(fromUserId);
    }

    public Optional<BookAccessRequest> approveRequest(Long requestId, LocalDateTime expiresAt, String comment) {
        Optional<BookAccessRequest> reqOpt = repository.findById(requestId);
        reqOpt.ifPresent(req -> {
            req.setStatus(BookAccessRequest.Status.APPROVED);
            req.setExpiresAt(expiresAt);
            req.setReason(comment);
            repository.save(req);
        });
        return reqOpt;
    }

    public Optional<BookAccessRequest> rejectRequest(Long requestId, String reason) {
        Optional<BookAccessRequest> reqOpt = repository.findById(requestId);
        reqOpt.ifPresent(req -> {
            req.setStatus(BookAccessRequest.Status.REJECTED);
            req.setReason(reason);
            repository.save(req);
        });
        return reqOpt;
    }

    public boolean hasActiveAccess(Long userId, Long bookId) {
        LocalDateTime now = LocalDateTime.now();
        return repository.findByBookIdAndFromUserId(bookId, userId).stream()
                .anyMatch(req -> req.getStatus() == BookAccessRequest.Status.APPROVED &&
                        (req.getExpiresAt() == null || req.getExpiresAt().isAfter(now)));
    }
} 