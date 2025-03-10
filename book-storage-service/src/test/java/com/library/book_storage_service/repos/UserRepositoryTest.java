package com.library.book_storage_service.repos;

import com.library.book_storage_service.models.User;
import com.library.book_storage_service.models.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
public class UserRepositoryTest {

    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Test
    void testSaveAndFindByUsername() {
        User user = new User(null, "testuser", "password123", Role.Librarian);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testExistsByUsername() {
        User user = new User(null, "uniqueuser", "password123", Role.Reader);
        userRepository.save(user);

        boolean exists = userRepository.existsByUsername("uniqueuser");
        assertThat(exists).isTrue();
    }

    @Test
    void testFindByUsername_NotFound() {
        Optional<User> user = userRepository.findByUsername("nonexistent");
        assertThat(user).isEmpty();
    }
}
