package com.library.book_storage_service.controllers;

import com.library.book_storage_service.DTO.BookDTO;
import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.services.CommandService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BookCommandController {

    private final CommandService commandService;
    public BookCommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    // ** Commands **

    @PostMapping("/create")
    public ResponseEntity<Object> createBook(@RequestBody final BookDTO book) {
        boolean savedBook = commandService.createBook(book);
        return ResponseEntity.status(savedBook ? HttpStatus.CREATED : HttpStatus.INTERNAL_SERVER_ERROR )
                .body(savedBook ? "Successfully saved" : "An error occurred");
    }
    @PutMapping("/book/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable Long id, @RequestBody final BookDTO updatedBook) {
        commandService.updateBook(id, updatedBook);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @DeleteMapping("/book/{id}")
    public ResponseEntity<Object> deleteBook(@PathVariable Long id) {
        commandService.deleteBook(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
