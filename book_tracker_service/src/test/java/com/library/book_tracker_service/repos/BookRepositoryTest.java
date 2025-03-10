package com.library.book_tracker_service.repos;


import com.library.book_tracker_service.models.Book;
import com.library.book_tracker_service.repos.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

@Testcontainers
@DataJpaTest
public class BookRepositoryTest {

    @Container
    private static final MySQLContainer mysql  = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private BookRepository bookRepository;

    @DynamicPropertySource
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }


    @Test
    void testSaveBook() {
        Book book = new Book("123456789", "Test Title", "Fiction", "A sample book", "John Doe");
        Book savedBook = bookRepository.save(book);

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123456789");
    }
    @Test
    void testFindByIdIn() {
        Book book1 = new Book(null, "1234567890", "Book One", "Fiction", "1111", "Author A");
        Book book2 = new Book(null, "0987654321", "Book Two", "Non-Fiction", "2222", "Author B");

        bookRepository.saveAll(List.of(book1, book2));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> result = bookRepository.findByIdIn(List.of(book1.getId(), book2.getId()), pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().anyMatch(book -> book.getTitle().equals("Book One")));
        assertTrue(result.getContent().stream().anyMatch(book -> book.getTitle().equals("Book Two")));
    }


}
