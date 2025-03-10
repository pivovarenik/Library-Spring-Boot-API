package com.library.book_tracker_service.services;

import com.library.book_tracker_service.DTO.BookDTO;
import com.library.book_tracker_service.models.Book;
import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.repos.BookRepository;
import com.library.book_tracker_service.repos.BookStatusRepository;
import com.library.book_tracker_service.mappers.BookMapper;
import com.library.book_tracker_service.mappers.BookStatusMapper;
import com.library.book_tracker_service.services.QueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class QueryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookStatusRepository bookStatusRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookStatusMapper bookStatusMapper;

    @InjectMocks
    private QueryService queryService;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        try(AutoCloseable au =MockitoAnnotations.openMocks(this)){
            pageable = PageRequest.of(0, 10);  // Example pageable (first page, 10 items per page)
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAvailableBooks_ShouldReturnBooks_WhenAvailableBooksExist() {
        // Setup mock data
        BookStatus bookStatus = new BookStatus();
        bookStatus.setBookId(1L);
        bookStatus.setStatus("AVAILABLE");
        List<BookStatus> availableStatuses = List.of(bookStatus);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");

        BookDTO bookDTO = new BookDTO("123456", "Book Title", "Genre", "Description", "Author");

        // Mock repository methods
        when(bookStatusRepository.findAllByStatus("AVAILABLE")).thenReturn(availableStatuses);
        when(bookRepository.findByIdIn(List.of(1L), pageable)).thenReturn(new PageImpl<>(List.of(book)));
        when(bookMapper.bookToBookDTO(book)).thenReturn(bookDTO);

        // Call the method
        Page<BookDTO> result = queryService.getAvailableBooks(pageable);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Book Title", result.getContent().get(0).title());
        verify(bookStatusRepository, times(1)).findAllByStatus("AVAILABLE");
        verify(bookRepository, times(1)).findByIdIn(List.of(1L), pageable);
    }

    @Test
    void getAvailableBooks_ShouldReturnEmptyPage_WhenNoAvailableBooksExist() {
        when(bookStatusRepository.findAllByStatus("AVAILABLE")).thenReturn(Collections.emptyList());

        Page<BookDTO> result = queryService.getAvailableBooks(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(bookStatusRepository, times(1)).findAllByStatus("AVAILABLE");
        verify(bookRepository, never()).findByIdIn(any(), any());
    }

    @Test
    void getAvailableBooks_ShouldReturnEmptyPage_WhenExceptionOccurs() {
        when(bookStatusRepository.findAllByStatus("AVAILABLE")).thenThrow(new RuntimeException("Database error"));

        Page<BookDTO> result = queryService.getAvailableBooks(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(bookStatusRepository, times(1)).findAllByStatus("AVAILABLE");
        verify(bookRepository, never()).findByIdIn(any(), any());
    }
}
