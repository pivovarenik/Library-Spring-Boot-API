package com.library.book_tracker_service.services;


import com.library.book_tracker_service.models.Book;
import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.repos.BookRepository;
import com.library.book_tracker_service.repos.BookStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueryService {
    private final BookStatusRepository bookStatusRepository;
    private final BookRepository bookRepository;

    public QueryService(BookStatusRepository bookStatusRepository, BookRepository bookRepository) {
        this.bookStatusRepository = bookStatusRepository;
        this.bookRepository = bookRepository;
    }

    public List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();

        List<BookStatus> availableStatuses = bookStatusRepository.findAllByStatus("AVAILABLE");

        List<Long> bookIds = availableStatuses.stream()
                .map(BookStatus::getBookId)
                .collect(Collectors.toList());

        books.addAll(bookRepository.findAllById(bookIds));

        return books;
    }
}
