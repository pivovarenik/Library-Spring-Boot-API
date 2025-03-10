package com.library.book_tracker_service.services;

import com.library.book_tracker_service.DTO.BookDTO;
import com.library.book_tracker_service.DTO.BookStatusDTO;
import com.library.book_tracker_service.mappers.BookMapper;
import com.library.book_tracker_service.mappers.BookStatusMapper;
import com.library.book_tracker_service.models.Book;
import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.repos.BookRepository;
import com.library.book_tracker_service.repos.BookStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandServiceTest {

    @Mock
    private BookStatusRepository bookStatusRepository;

    @Mock
    private BookStatusMapper bookStatusMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CommandService commandService;

    private BookStatus bookStatus;
    private Book book;
    private BookDTO bookDTO;
    private BookStatusDTO bookStatusDTO;

    @BeforeEach
    void setUp() {
        try(AutoCloseable au = MockitoAnnotations.openMocks(this)){
            bookDTO = new BookDTO("1232313123", "Book Title", "Author", "12345","description");
            book = new Book(1L, "1232313123", "Book Title", "Author", "12345","description");
            bookStatus = new BookStatus(1L, "AVAILABLE", LocalDateTime.now(), LocalDateTime.now().plusDays(7));
            bookStatusDTO = new BookStatusDTO("AVAILABLE", LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void createRecord_ShouldCreateNewRecord() {
        when(bookStatusRepository.existsById(1L)).thenReturn(false);
        when(bookStatusRepository.save(any(BookStatus.class))).thenReturn(bookStatus);

        BookStatus createdRecord = commandService.createRecord(1L);

        assertNotNull(createdRecord);
        assertEquals("AVAILABLE", createdRecord.getStatus());
        verify(bookStatusRepository, times(1)).save(any(BookStatus.class));
    }

    @Test
    void createRecord_ShouldThrowIllegalArgumentException_WhenRecordExists() {
        when(bookStatusRepository.existsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> commandService.createRecord(1L));
    }

    @Test
    void updateBookStatus_ShouldThrowEntityNotFoundException_WhenBookNotFound() {
        when(bookStatusRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commandService.updateBookStatus(1L, "BORROWED", null, null));
    }

    @Test
    void updateBookStatus_ShouldThrowEntityNotFoundException_WhenBookDeleted() {
        bookStatus.setStatus("DELETED");
        when(bookStatusRepository.findById(1L)).thenReturn(Optional.of(bookStatus));

        assertThrows(EntityNotFoundException.class, () -> commandService.updateBookStatus(1L, "BORROWED", null, null));
    }

    @Test
    void deleteBookStatus_ShouldMarkBookAsDeleted() {
        doNothing().when(bookStatusRepository).markBookAsDeleted(1L);

        commandService.deleteBookStatus(1L);

        verify(bookStatusRepository, times(1)).markBookAsDeleted(1L);
    }

    @Test
    void addBook_ShouldSaveBook() {
        when(bookMapper.bookDTOToBook(any(BookDTO.class))).thenReturn(book);

        commandService.addBook(bookDTO);

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void deleteBook_ShouldDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);

        commandService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }




    @Test
    void updateBook_ShouldThrowEntityNotFoundException_WhenBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commandService.updateBook(1L, bookDTO));
    }
}
