package com.library.book_tracker_service;

import com.library.book_tracker_service.services.CommandService;
import com.library.book_tracker_service.services.MessageConsumer;
import com.library.book_tracker_service.services.QueryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class MessageConsumerTest {

    @MockitoBean
    private CommandService commandService;

    @MockitoBean
    private QueryService queryService;

    @Autowired
    private MessageConsumer messageConsumer;

    @Test
    void consume_AddCommand_Success() throws Exception {
        String message = "{\"id\":1, \"command\":\"ADD\"}";

        messageConsumer.consume(message);

        Mockito.verify(commandService, Mockito.times(1)).createRecord(1L);
    }

    @Test
    void consume_DeleteCommand_Success() throws Exception {
        String message = "{\"id\":1, \"command\":\"DELETE\"}";

        messageConsumer.consume(message);

        Mockito.verify(commandService, Mockito.times(1)).deleteBookStatus(1L);
    }

    @Test
    void consume_InvalidMessage_HandlesGracefully() {
        String invalidMessage = "Invalid JSON";

        Assertions.assertDoesNotThrow(() -> messageConsumer.consume(invalidMessage));
    }
}
