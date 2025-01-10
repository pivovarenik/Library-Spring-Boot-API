package com.library.book_storage_service;

import com.library.book_storage_service.controllers.BookController;
import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.services.CommandService;
import com.library.book_storage_service.services.JwtTokenApi;
import com.library.book_storage_service.services.QueryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommandService commandService;

    @MockitoBean
    private QueryService queryService;

    @MockitoBean
    private JwtTokenApi jwtTokenApi;

    @Test
    void getAllBooks_ShouldReturnEmptyList() throws Exception {
        when(queryService.getAllBooks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/library"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getBookById_ShouldReturnBook_WhenExists() throws Exception {
        Book book = new Book(1L,"978-3-16-148410-0", "1984", "Дистопия", "Роман-антиутопия Джорджа Оруэлла", "Джордж Оруэлл");
        when(queryService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/library/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.isbn").value("978-3-16-148410-0"));
    }

    @Test
    void getBookById_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        when(queryService.getBookById(1L)).thenThrow(new EntityNotFoundException("Book not found"));

        mockMvc.perform(get("/api/library/book/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }

    @Test
    void createBook_ShouldReturnCreatedBook_WhenAuthorized() throws Exception {
        Book book = new Book("978-3-16-148410-0", "1984", "Дистопия", "Роман-антиутопия Джорджа Оруэлла", "Джордж Оруэлл");
        String token = "Bearer valid.token.string";

        when(jwtTokenApi.validateToken("valid.token.string")).thenReturn("valid");
        when(commandService.createBook(Mockito.any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/library")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                " \"id\":\"1\",\n"+
                                "  \"isbn\": \"323-0-1234-5678-9\",\n" +
                                "  \"title\": \"1232k\",\n" +
                                "  \"genre\": \"232\",\n" +
                                "  \"description\": \"A test book\",\n" +
                                "  \"author\": \"Author Name\"\n" +
                                "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.isbn").value("978-3-16-148410-0"));
    }

    @Test
    void createBook_ShouldReturnUnauthorized_WhenTokenIsInvalid() throws Exception {
        String token = "Bearer invalid.token.string";

        when(jwtTokenApi.validateToken("invalid.token.string")).thenReturn("invalid");

        mockMvc.perform(post("/api/library")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Book\", \"isbn\": \"123456789\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized due to invalid"));
    }

    @Test
    void deleteBook_ShouldReturnNoContent_WhenBookDeleted() throws Exception {
        mockMvc.perform(delete("/api/library/book/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateBook_ShouldReturnNoContent_WhenBookUpdated() throws Exception {
        mockMvc.perform(put("/api/library/book/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Book\", \"isbn\": \"987654321\"}"))
                .andExpect(status().isNoContent());
    }
}
