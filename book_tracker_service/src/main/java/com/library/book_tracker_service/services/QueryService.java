package com.library.book_tracker_service.services;


import com.library.book_tracker_service.DTO.BookDTO;
import com.library.book_tracker_service.DTO.BookStatusDTO;
import com.library.book_tracker_service.mappers.BookMapper;
import com.library.book_tracker_service.mappers.BookStatusMapper;
import com.library.book_tracker_service.models.Book;
import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.repos.BookRepository;
import com.library.book_tracker_service.repos.BookStatusRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueryService {
    private final BookStatusRepository bookStatusRepository;
    private final BookStatusMapper bookStatusMapper;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    public QueryService(BookRepository bookRepository,BookMapper bookMapper,BookStatusRepository bookStatusRepository, BookStatusMapper bookStatusMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.bookStatusRepository = bookStatusRepository;
        this.bookStatusMapper = bookStatusMapper;
    }

    public Page<BookDTO> getAvailableBooks(Pageable pageable) {
        try{
            List<BookStatus> availableStatuses = bookStatusRepository.findAllByStatus("AVAILABLE");
            if (availableStatuses.isEmpty()) {
                return Page.empty();
            }
            List<Long> bookIds = availableStatuses.stream().map(BookStatus::getBookId).toList();
            Page<Book> books = bookRepository.findByIdIn(bookIds,pageable);
            return books.map(bookMapper::bookToBookDTO);
        }
        catch (Exception e){
            e.printStackTrace();
            return Page.empty();
        }

    }
}
