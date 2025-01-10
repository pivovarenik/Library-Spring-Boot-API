package com.library.book_storage_service.controllers;

import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.services.CommandService;
import com.library.book_storage_service.services.JwtTokenApi;
import com.library.book_storage_service.services.QueryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class BookController {

    private final CommandService commandService;
    private final QueryService queryService;
    private final JwtTokenApi jwtTokenApi;

    public BookController(final CommandService commandService, final QueryService queryService, JwtTokenApi jwtTokenApi) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.jwtTokenApi = jwtTokenApi;
    }

    //  **Queries**

    @GetMapping("")
    public List<Book> getAllBooks() {
        return queryService.getAllBooks();
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable Long id) {
        try {
            Book book = queryService.getBookById(id);
            return ResponseEntity.ok(book);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }
    @GetMapping("/book")
    public ResponseEntity<Object> getBookByISBN(@RequestParam(required = false) String isbn) {
        try {
            Book book = queryService.getBookByISBN(isbn);
            return ResponseEntity.ok(book);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }

    // ** Commands **

    @PostMapping("")
    public ResponseEntity<Object> createBook(@RequestBody final Book book, @RequestHeader(value = "Authorization", required = false) final String token) {
        if(token ==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is required to proceed");
        }
        else{
            String jwtToken = token.substring(7);
            String tokenCheckResult = jwtTokenApi.validateToken(jwtToken);
            if(tokenCheckResult.equalsIgnoreCase("valid")){
                try {
                    Book savedBook = commandService.createBook(book);
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create book: " + e.getMessage());
                }
            }
            else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized due to "+tokenCheckResult);
        }
    }
    @PutMapping("/book/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable Long id, @RequestBody final Book updatedBook) {
        try {
            commandService.updateBook(id, updatedBook);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update book: " + e.getMessage());
        }
    }
    @DeleteMapping("/book/{id}")
    public ResponseEntity<Object> deleteBook(@PathVariable Long id) {
        try {
            commandService.deleteBook(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete book: " + e.getMessage());
        }
    }

}
