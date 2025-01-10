package com.library.book_storage_service;

import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.repos.BookRepository;
import com.library.book_storage_service.services.QueryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private QueryService queryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBooks_ReturnsListOfBooks() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book));

        List<Book> books = queryService.getAllBooks();

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Test Book", books.get(0).getTitle());

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById_BookExists_ReturnsBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book foundBook = queryService.getBookById(1L);

        assertNotNull(foundBook);
        assertEquals("Test Book", foundBook.getTitle());

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookById_BookDoesNotExist_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> queryService.getBookById(1L));

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookByISBN_BookExists_ReturnsBook() {
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("12345");

        when(bookRepository.findByIsbn("12345")).thenReturn(Optional.of(book));

        Book foundBook = queryService.getBookByISBN("12345");

        assertNotNull(foundBook);
        assertEquals("12345", foundBook.getIsbn());

        verify(bookRepository, times(1)).findByIsbn("12345");
    }

    @Test
    void getBookByISBN_BookDoesNotExist_ThrowsException() {
        when(bookRepository.findByIsbn("12345")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> queryService.getBookByISBN("12345"));

        verify(bookRepository, times(1)).findByIsbn("12345");
    }
}
