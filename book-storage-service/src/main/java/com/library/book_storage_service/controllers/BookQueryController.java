package com.library.book_storage_service.controllers;

import com.library.book_storage_service.DTO.BookDTO;
import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.services.CommandService;
import com.library.book_storage_service.services.QueryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
public class BookQueryController {

    private final QueryService queryService;

    public BookQueryController(final QueryService queryService) {

        this.queryService = queryService;
    }

    //  **Queries**

    @GetMapping("")
    public ResponseEntity<Object> getAllBooks(Pageable pageable) {
        return ResponseEntity.ok(queryService.getAllBooks(pageable));
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable Long id) {
        BookDTO book = queryService.getBookById(id);
        return ResponseEntity.ok(book);
    }
    @GetMapping("/book")
    public ResponseEntity<Object> getBookByISBN(@RequestParam(required = false) String isbn) {
        BookDTO book = queryService.getBookByISBN(isbn);
        return ResponseEntity.ok(book);

    }
}
