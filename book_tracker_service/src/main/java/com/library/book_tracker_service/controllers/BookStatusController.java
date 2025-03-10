package com.library.book_tracker_service.controllers;

import com.library.book_tracker_service.DTO.BookDTO;
import com.library.book_tracker_service.DTO.BookStatusDTO;
import com.library.book_tracker_service.mappers.BookStatusMapper;
import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.services.CommandService;
import com.library.book_tracker_service.services.QueryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trackerApi")
public class BookStatusController {

    private final QueryService queryService;
    private final CommandService commandService;
    private final BookStatusMapper bookStatusMapper;

    public BookStatusController(QueryService queryService, CommandService commandService, BookStatusMapper bookStatusMapper) {
        this.queryService = queryService;
        this.commandService = commandService;
        this.bookStatusMapper = bookStatusMapper;
    }

    //** Commands **
    @PostMapping("/create")
    public ResponseEntity<Object> createRecord(@RequestBody(required = false) final Long id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID must not be null");
        }
        BookStatus savedBook = commandService.createRecord(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookStatusMapper.bookStatusToBookStatusDTO(savedBook));
    }
    @DeleteMapping("/del/{id}")
    public ResponseEntity<Object> deleteRecord(@PathVariable final Long id) {
        commandService.deleteBookStatus(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PutMapping("/change/{id}")
    public ResponseEntity<Object> changeBookStatus(@PathVariable final Long id, @RequestBody final BookStatus status) {
        commandService.updateBookStatus(id,status.getStatus(),status.getBorrowedAt(),status.getDueAt());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    //** Queries **
    @GetMapping("")
    public ResponseEntity<Page<BookDTO>> getAvailableBooks(Pageable pageable) {
        return ResponseEntity.ok(queryService.getAvailableBooks(pageable));
    }

}
