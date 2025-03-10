package com.library.book_storage_service.services;

import com.library.book_storage_service.DTO.BookDTO;
import com.library.book_storage_service.mappers.BookMapper;
import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.repos.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class QueryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private QueryService queryService;

    private Book book;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        try(AutoCloseable au = openMocks(this)){

            bookDTO = new BookDTO("1232313123", "Book Title", "Sample Book", "12345","My author");
            book = new Book(1L, "1232313123", "Book Title", "Sample Book", "12345","My author");

            when(bookMapper.bookDTOToBook(bookDTO)).thenReturn(book);
            when(bookMapper.bookToBookDTO(book)).thenReturn(bookDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAllBooks() {
        Page<Book> books = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(books);

        Page<BookDTO> result = queryService.getAllBooks(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Sample Book", result.getContent().getFirst().author());
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetBookById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDTO result = queryService.getBookById(1L);

        assertNotNull(result);
        assertEquals("Sample Book", result.author());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> queryService.getBookById(1L));
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookByISBN_Success() {
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(book));

        BookDTO result = queryService.getBookByISBN("1234567890");

        assertNotNull(result);
        assertEquals("Sample Book", result.author());
        verify(bookRepository, times(1)).findByIsbn("1234567890");
    }

    @Test
    void testGetBookByISBN_NotFound() {
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> queryService.getBookByISBN("1234567890"));
        verify(bookRepository, times(1)).findByIsbn("1234567890");
    }
}
