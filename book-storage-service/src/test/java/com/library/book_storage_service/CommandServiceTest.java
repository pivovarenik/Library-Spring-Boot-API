package com.library.book_storage_service;

import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.repos.BookRepository;
import com.library.book_storage_service.services.CommandService;
import com.library.book_storage_service.services.MessageProducer;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private CommandService commandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBook_SavesBookAndSendsMessage() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookRepository.save(book)).thenReturn(book);

        Book createdBook = commandService.createBook(book);

        assertNotNull(createdBook);
        assertEquals("Test Book", createdBook.getTitle());

        verify(bookRepository, times(1)).save(book);
        verify(messageProducer, times(1)).sendMessage(eq("books"), contains("ADD"));
    }

    @Test
    void updateBook_BookExists_UpdatesAndSavesBook() {
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old Title");

        Book updatedBook = new Book();
        updatedBook.setTitle("New Title");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));

        commandService.updateBook(1L, updatedBook);

        assertEquals("New Title", existingBook.getTitle());
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    void updateBook_BookDoesNotExist_ThrowsException() {
        Book updatedBook = new Book();
        updatedBook.setTitle("New Title");

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commandService.updateBook(1L, updatedBook));

        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteBook_BookExists_DeletesBookAndSendsMessage() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        commandService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
        verify(messageProducer, times(1)).sendMessage(eq("books"), contains("DELETE"));
    }

}
