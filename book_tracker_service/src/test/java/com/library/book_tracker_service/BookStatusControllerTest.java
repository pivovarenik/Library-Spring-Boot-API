package com.library.book_tracker_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.book_tracker_service.config.JacksonConfig;
import com.library.book_tracker_service.models.BookStatus;
import com.library.book_tracker_service.services.CommandService;
import com.library.book_tracker_service.services.QueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommandService commandService;

    @MockitoBean
    private QueryService queryService;

    @Test
    void createRecord_Success() throws Exception {
        mockMvc.perform(post("/trackerApi/create")
                        .content("1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteRecord_Success() throws Exception {
        mockMvc.perform(delete("/trackerApi/del/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void changeBookStatus_Success() throws Exception {
        BookStatus bookStatus = new BookStatus(1L, "BORROWED", LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        ObjectMapper objectMapper = JacksonConfig.getObjectMapper();
        mockMvc.perform(put("/trackerApi/change/1")
                        .content(objectMapper.writeValueAsString(bookStatus))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAvailableBooks_Success() throws Exception {
        mockMvc.perform(get("/trackerApi"))
                .andExpect(status().isOk());
    }
}
