package com.library.book_tracker_service.controllers;


import com.library.book_tracker_service.models.Book;
import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.services.CommandService;
import com.library.book_tracker_service.services.QueryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trackerApi")
public class BookStatusController {

    private final QueryService queryService;
    private final CommandService commandService;

    public BookStatusController(QueryService queryService, CommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    //** Commands **
    @PostMapping("/create")
    public ResponseEntity<Object> createRecord(@RequestBody(required = false) final Long id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID must not be null");
        }
        try {
            BookStatus savedBook = commandService.createRecord(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create a record: " + e.getMessage());
        }
    }
    @DeleteMapping("/del/{id}")
    public ResponseEntity<Object> deleteRecord(@PathVariable final Long id) {
        try {
            commandService.deleteBookStatus(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete book: " + e.getMessage());
        }
    }
    @PutMapping("/change/{id}")
    public ResponseEntity<Object> changeBookStatus(@PathVariable final Long id, @RequestBody final BookStatus status) {
        try {
            commandService.updateBookStatus(id,status.getStatus(),status.getBorrowedAt(),status.getDueAt());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update book: " + e.getMessage());
        }
    }
    //** Queries **
    @GetMapping("")
    public List<Book> getAvailableBooks() {
        return queryService.getAvailableBooks();
    }

}
