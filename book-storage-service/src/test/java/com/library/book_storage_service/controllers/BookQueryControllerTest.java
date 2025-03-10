package com.library.book_storage_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.book_storage_service.DTO.BookDTO;
import com.library.book_storage_service.exceptions.GlobalExceptionHandler;
import com.library.book_storage_service.services.JwtCore;
import com.library.book_storage_service.services.QueryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookQueryController.class)
@Import(GlobalExceptionHandler.class)
class BookQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JwtCore jwtCore;
    @MockitoBean
    private QueryService queryService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        bookDTO = new BookDTO("1234567890", "Test Book", "Test Author", "Test Genre", "Test Description");
    }

    @Test
    @WithMockUser(username = "user", roles = {"Librarian"})
    void testGetAllBooks_Success() throws Exception {
        when(queryService.getAllBooks(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(bookDTO)));

        mockMvc.perform(get("/library")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].isbn").value("1234567890"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"Librarian"})
    void testGetBookById_Success() throws Exception {
        when(queryService.getBookById(anyLong())).thenReturn(bookDTO);

        mockMvc.perform(get("/library/book/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("1234567890"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"Librarian"})
    void testGetBookById_NotFound() throws Exception {
        when(queryService.getBookById(anyLong())).thenThrow(new EntityNotFoundException("Book not found"));

        mockMvc.perform(get("/library/book/99")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"Librarian"})
    void testGetBookByISBN_Success() throws Exception {
        when(queryService.getBookByISBN(Mockito.anyString())).thenReturn(bookDTO);

        mockMvc.perform(get("/library/book")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("isbn", "1234567890")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("1234567890"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"Librarian"})
    void testGetBookByISBN_NotFound() throws Exception {
        when(queryService.getBookByISBN(Mockito.anyString())).thenThrow(new EntityNotFoundException("Book not found"));

        mockMvc.perform(get("/library/book")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("isbn", "9876543210")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
