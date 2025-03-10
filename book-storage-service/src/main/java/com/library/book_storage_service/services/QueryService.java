package com.library.book_storage_service.services;

import com.library.book_storage_service.DTO.BookDTO;
import com.library.book_storage_service.mappers.BookMapper;
import com.library.book_storage_service.mappers.UserMapper;
import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.repos.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QueryService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    public QueryService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper= bookMapper;
    }

    public Page<BookDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::bookToBookDTO);
    }

    public BookDTO getBookById(Long id) {
        return bookRepository.findById(id).map(bookMapper::bookToBookDTO)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    public BookDTO getBookByISBN(String isbn) {
        return bookRepository.findByIsbn(isbn).map(bookMapper::bookToBookDTO)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }
}