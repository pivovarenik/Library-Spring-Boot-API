package com.library.book_storage_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.book_storage_service.DTO.BookDTO;
import com.library.book_storage_service.services.CommandService;
import com.library.book_storage_service.services.JwtCore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookCommandController.class)
class BookCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommandService commandService;
    @MockitoBean
    private JwtCore jwtCore;

    private BookDTO bookDTO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        bookDTO = new BookDTO("1234567890","Test Book","Test Author","Test Genre","Test Description");
    }

    @Test
    @WithMockUser(username = "user", roles = {"Librarian"})
    void testCreateBook_Success() throws Exception {
        when(commandService.createBook(any(BookDTO.class))).thenReturn(true);

        mockMvc.perform(post("/api/create")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO))
                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("Librarian")))
                .andExpect(status().isCreated())
                .andExpect(content().string("Successfully saved"));

        verify(commandService, times(1)).createBook(any(BookDTO.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Librarian"})
    void testUpdateBook_Success() throws Exception {
        doNothing().when(commandService).updateBook(anyLong(), any(BookDTO.class));

        mockMvc.perform(put("/api/book/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(bookDTO))
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("Librarian")))
                .andExpect(status().isNoContent());

        verify(commandService, times(1)).updateBook(anyLong(), any(BookDTO.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Librarian"})
    void testDeleteBook_Success() throws Exception {
        doNothing().when(commandService).deleteBook(anyLong());

        mockMvc.perform(delete("/api/book/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("Librarian")))
                .andExpect(status().isNoContent());

        verify(commandService, times(1)).deleteBook(anyLong());
    }
}
