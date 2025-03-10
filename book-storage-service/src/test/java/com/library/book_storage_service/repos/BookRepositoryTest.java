package com.library.book_storage_service.repos;

import com.library.book_storage_service.models.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

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
    void testSaveAndFindByIsbn() {

        Book book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setGenre("Test Genre");
        book.setDescription("Test Description");


        bookRepository.save(book);


        Optional<Book> foundBook = bookRepository.findByIsbn("1234567890");


        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Test Book");
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
    void testFindByIsbn() {
        Book book = new Book("123456789", "Test Title", "Fiction", "A sample book", "John Doe");
        bookRepository.save(book);

        Optional<Book> foundBook = bookRepository.findByIsbn("123456789");

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Test Title");
    }

    @Test
    void testFindAllBooks() {
        Book book1 = new Book("111111111", "Book One", "Sci-Fi", "Desc 1", "Author 1");
        Book book2 = new Book("222222222", "Book Two", "Drama", "Desc 2", "Author 2");

        bookRepository.save(book1);
        bookRepository.save(book2);

        List<Book> books = bookRepository.findAll();

        assertThat(books).hasSize(4);
    }

    @Test
    void testDeleteBook() {
        Book book = new Book("333333333", "Book to Delete", "Horror", "Desc", "Author");
        bookRepository.save(book);

        bookRepository.delete(book);
        Optional<Book> deletedBook = bookRepository.findByIsbn("333333333");

        assertThat(deletedBook).isEmpty();
    }

    @Test
    void testFindByIsbn_NotFound() {
        Optional<Book> book = bookRepository.findByIsbn("999999999");

        assertThat(book).isEmpty();
    }
}
