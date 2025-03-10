package com.library.book_tracker_service.services;


import com.library.book_tracker_service.DTO.BookDTO;
import com.library.book_tracker_service.DTO.BookStatusDTO;
import com.library.book_tracker_service.mappers.BookMapper;
import com.library.book_tracker_service.mappers.BookStatusMapper;
import com.library.book_tracker_service.models.Book;
import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.repos.BookRepository;
import com.library.book_tracker_service.repos.BookStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommandService {
    private final BookStatusRepository bookStatusRepository;
    private final BookStatusMapper bookStatusMapper;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public CommandService(BookStatusRepository bookStatusRepository, BookStatusMapper bookStatusMapper, BookRepository bookRepository,BookMapper bookMapper) {
        this.bookStatusRepository = bookStatusRepository;
        this.bookStatusMapper = bookStatusMapper;
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
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
    public BookStatusDTO updateBookStatus(Long bookId, String newStatus, LocalDateTime borrowedAt, LocalDateTime dueAt) {
        return bookStatusRepository.findById(bookId).map(bookStatus -> {
            if(!bookStatus.getStatus().equals("DELETED")) {
                bookStatus.setStatus(newStatus);
                bookStatus.setBorrowedAt(borrowedAt);
                bookStatus.setDueAt(dueAt);
                return bookStatusMapper.bookStatusToBookStatusDTO(bookStatusRepository.save(bookStatus));
            }
           else throw new EntityNotFoundException("Book was deleted");
        }).orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }
    public void deleteBookStatus(Long bookId) {
        bookStatusRepository.markBookAsDeleted(bookId);
    }

    public void addBook(BookDTO book){
        bookRepository.save(bookMapper.bookDTOToBook(book));
    }
    public void deleteBook(Long bookId){
        bookRepository.deleteById(bookId);
    }

    public void updateBook(Long id, BookDTO updatedBook) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        bookMapper.updateBookDTO(updatedBook, existingBook);
        bookRepository.save(existingBook);
    }
}
