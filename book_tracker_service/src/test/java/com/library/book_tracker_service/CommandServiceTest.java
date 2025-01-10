package com.library.book_tracker_service;

import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.repos.BookStatusRepository;
import com.library.book_tracker_service.services.CommandService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
class CommandServiceTest {

    @MockitoBean
    private BookStatusRepository bookStatusRepository;

    @Autowired
    private CommandService commandService;

    @Test
    void createRecord_Success() {
        Long bookId = 1L;

        Mockito.when(bookStatusRepository.existsById(bookId)).thenReturn(false);
        Mockito.when(bookStatusRepository.save(Mockito.any(BookStatus.class)))
                .thenReturn(new BookStatus(bookId, "AVAILABLE", null, null));

        BookStatus result = commandService.createRecord(bookId);

        Assertions.assertEquals("AVAILABLE", result.getStatus());
        Mockito.verify(bookStatusRepository, Mockito.times(1)).save(Mockito.any(BookStatus.class));
    }

    @Test
    void createRecord_ThrowsException_WhenBookAlreadyExists() {
        Long bookId = 1L;

        Mockito.when(bookStatusRepository.existsById(bookId)).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> commandService.createRecord(bookId));
    }

    @Test
    void updateBookStatus_Success() {
        Long bookId = 1L;
        BookStatus existingStatus = new BookStatus(bookId, "AVAILABLE", null, null);
        LocalDateTime borrowedAt = LocalDateTime.now();
        LocalDateTime dueAt = borrowedAt.plusDays(7);

        Mockito.when(bookStatusRepository.findById(bookId)).thenReturn(Optional.of(existingStatus));
        Mockito.when(bookStatusRepository.save(Mockito.any(BookStatus.class)))
                .thenReturn(existingStatus);

        BookStatus result = commandService.updateBookStatus(bookId, "BORROWED", borrowedAt, dueAt);

        Assertions.assertEquals("BORROWED", result.getStatus());
        Assertions.assertEquals(borrowedAt, result.getBorrowedAt());
        Assertions.assertEquals(dueAt, result.getDueAt());
    }

    @Test
    void updateBookStatus_ThrowsException_WhenBookDeleted() {
        Long bookId = 1L;
        BookStatus deletedStatus = new BookStatus(bookId, "DELETED", null, null);

        Mockito.when(bookStatusRepository.findById(bookId)).thenReturn(Optional.of(deletedStatus));

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                commandService.updateBookStatus(bookId, "AVAILABLE", null, null));
    }

    @Test
    void deleteBookStatus_Success() {
        Long bookId = 1L;

        commandService.deleteBookStatus(bookId);

        Mockito.verify(bookStatusRepository, Mockito.times(1)).markBookAsDeleted(bookId);
    }
}
