package com.library.book_tracker_service.repos;

import com.library.book_tracker_service.models.BookStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static java.lang.System.setProperty;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@DataJpaTest
class BookStatusRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private BookStatusRepository bookStatusRepository;

    @DynamicPropertySource
    static void setup(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @AfterAll
    static void tearDown() {
        mysql.stop();
    }

    @Test
    void testFindAllByStatus() {
        BookStatus book1 = new BookStatus(1L, "AVAILABLE",null,null);
        BookStatus book2 = new BookStatus(2L, "CHECKED_OUT",null,null);
        BookStatus book3 = new BookStatus(3L,"AVAILABLE",null,null);

        bookStatusRepository.saveAll(List.of(book1, book2, book3));

        List<BookStatus> availableBooks = bookStatusRepository.findAllByStatus("AVAILABLE");

        assertEquals(2, availableBooks.size());
        assertTrue(availableBooks.stream().allMatch(book -> book.getStatus().equals("AVAILABLE")));
    }

    @Test
    void testMarkBookAsDeleted() {
        BookStatus book = new BookStatus(3L, "AVAILABLE",null,null);
        bookStatusRepository.saveAndFlush(book);

        assertTrue(bookStatusRepository.findById(book.getBookId()).isPresent());

        bookStatusRepository.markBookAsDeleted(book.getBookId());
        bookStatusRepository.flush();

        BookStatus updatedBook = bookStatusRepository.findById(book.getBookId()).orElseThrow();
        assertEquals("DELETED", updatedBook.getStatus());
    }
}
