package com.library.book_tracker_service;

import com.library.book_tracker_service.models.Book;
import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.repos.BookRepository;
import com.library.book_tracker_service.repos.BookStatusRepository;
import com.library.book_tracker_service.services.QueryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

@SpringBootTest
class QueryServiceTest {

    @MockitoBean
    private BookStatusRepository bookStatusRepository;

    @MockitoBean
    private BookRepository bookRepository;

    @Autowired
    private QueryService queryService;

    @Test
    void getAvailableBooks_Success() {
        List<Long> bookIds = List.of(1L, 2L);
        List<BookStatus> statuses = bookIds.stream()
                .map(id -> new BookStatus(id, "AVAILABLE", null, null))
                .toList();
        List<Book> books = bookIds.stream()
                .map(id -> new Book(id, "Title " + id, "Author " + id, "Genre", "Description",""))
                .toList();

        Mockito.when(bookStatusRepository.findAllByStatus("AVAILABLE")).thenReturn(statuses);
        Mockito.when(bookRepository.findAllById(bookIds)).thenReturn(books);

        List<Book> result = queryService.getAvailableBooks();

        Assertions.assertEquals(2, result.size());
    }
}
