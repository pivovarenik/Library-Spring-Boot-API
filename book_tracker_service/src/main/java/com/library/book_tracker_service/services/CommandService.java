package com.library.book_tracker_service.services;


import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.repos.BookStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommandService {
    private final BookStatusRepository bookStatusRepository;

    public CommandService(BookStatusRepository bookStatusRepository) {
        this.bookStatusRepository = bookStatusRepository;
    }

    public BookStatus createRecord(Long id) {
        if (bookStatusRepository.existsById(id)) {
            throw new IllegalArgumentException("Record with this ID already exists");
        }
        BookStatus bookStatus = BookStatus
                .builder()
                .bookId(id)
                .status("AVAILABLE")
                .dueAt(null)
                .borrowedAt(null)
                .build();
        return bookStatusRepository.save(bookStatus);
    }
    public BookStatus updateBookStatus(Long bookId, String newStatus, LocalDateTime borrowedAt, LocalDateTime dueAt) {
        return bookStatusRepository.findById(bookId).map(bookStatus -> {
            if(!bookStatus.getStatus().equals("DELETED")) {
                bookStatus.setStatus(newStatus);
                bookStatus.setBorrowedAt(borrowedAt);
                bookStatus.setDueAt(dueAt);
                return bookStatusRepository.save(bookStatus);
            }
           else throw new EntityNotFoundException("Book was deleted");
        }).orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }
    public void deleteBookStatus(Long bookId) {
        bookStatusRepository.markBookAsDeleted(bookId);
    }
}
