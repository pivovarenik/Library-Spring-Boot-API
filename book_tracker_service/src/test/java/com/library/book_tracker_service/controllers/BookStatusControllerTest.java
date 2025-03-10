package com.library.book_tracker_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.library.book_tracker_service.DTO.BookDTO;
import com.library.book_tracker_service.DTO.BookStatusDTO;
import com.library.book_tracker_service.mappers.BookStatusMapper;
import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.services.CommandService;
import com.library.book_tracker_service.services.QueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookStatusController.class)
class BookStatusControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private QueryService queryService;

    @MockitoBean
    private CommandService commandService;

    @MockitoBean
    private BookStatusMapper bookStatusMapper;

    @InjectMocks
    private BookStatusController bookStatusController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private BookStatus bookStatus;
    private BookStatusDTO bookStatusDTO;

    @BeforeEach
    void setUp() {
        try(AutoCloseable au = MockitoAnnotations.openMocks(this)){
            mockMvc = MockMvcBuilders.standaloneSetup(bookStatusController).build();
            objectMapper.registerModule(new JavaTimeModule());
            bookStatus = new BookStatus(1L, "AVAILABLE", LocalDateTime.now(), LocalDateTime.now().plusDays(7));
            bookStatusDTO = new BookStatusDTO("AVAILABLE", LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateRecord_Success() throws Exception {
        when(commandService.createRecord(1L)).thenReturn(bookStatus);
        when(bookStatusMapper.bookStatusToBookStatusDTO(bookStatus)).thenReturn(bookStatusDTO);

        mockMvc.perform(post("/trackerApi/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void testCreateRecord_BadRequest() throws Exception {
        mockMvc.perform(post("/trackerApi/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteRecord_Success() throws Exception {
        doNothing().when(commandService).deleteBookStatus(1L);

        mockMvc.perform(delete("/trackerApi/del/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testChangeBookStatus_Success() throws Exception {
        when(commandService.updateBookStatus(eq(1L), any(), any(), any())).thenReturn(null);

        mockMvc.perform(put("/trackerApi/change/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookStatus)))
                .andExpect(status().isNoContent());
    }
}
