package com.library.book_tracker_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.book_tracker_service.DTO.BookDTO;
import com.library.book_tracker_service.models.Book;
import com.library.book_tracker_service.services.CommandService;
import com.library.book_tracker_service.services.QueryService;
import com.library.book_tracker_service.mappers.BookMapper;
import com.library.book_tracker_service.services.models.KafkaBookMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.annotation.KafkaListener;

import static com.library.book_tracker_service.services.models.Command.*;
import static org.mockito.Mockito.*;

class MessageConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CommandService commandService;

    @Mock
    private QueryService queryService;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private MessageConsumer messageConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsume_AddCommand() throws Exception {
        // Prepare mock data
        KafkaBookMessage kafkaMessage = new KafkaBookMessage();
        kafkaMessage.setCommand(ADD);
        kafkaMessage.setId(1L);
        kafkaMessage.setBook(new Book());

        String message = "mockMessage";

        when(objectMapper.readValue(message, KafkaBookMessage.class)).thenReturn(kafkaMessage);

        messageConsumer.consume(message);

        verify(commandService, times(1)).createRecord(kafkaMessage.getId());
        verify(commandService, times(1)).addBook(any());
    }

    @Test
    void testConsume_DeleteCommand() throws Exception {
        KafkaBookMessage kafkaMessage = new KafkaBookMessage();
        kafkaMessage.setCommand(DELETE);
        kafkaMessage.setId(2L);

        String message = "mockDeleteMessage";

        when(objectMapper.readValue(message, KafkaBookMessage.class)).thenReturn(kafkaMessage);

        messageConsumer.consume(message);

        verify(commandService, times(1)).deleteBookStatus(kafkaMessage.getId());
        verify(commandService, times(1)).deleteBook(kafkaMessage.getId());
    }

    @Test
    void testConsume_UpdateCommand() throws Exception {
        KafkaBookMessage kafkaMessage = new KafkaBookMessage();
        kafkaMessage.setCommand(UPDATE);
        kafkaMessage.setId(3L);
        Book book = new Book();
        book.setId(3L);
        book.setTitle("Updated Book Title");
        kafkaMessage.setBook(book);

        String message = "mockUpdateMessage";

        when(objectMapper.readValue(message, KafkaBookMessage.class)).thenReturn(kafkaMessage);

        BookDTO bookDTO = new BookDTO("3L", "Updated Book Title", "Updated Genre", "Updated Description", "Updated Author");
        when(bookMapper.bookToBookDTO(book)).thenReturn(bookDTO);

        messageConsumer.consume(message);
        
        verify(commandService, times(1)).updateBook(eq(kafkaMessage.getId()), eq(bookDTO));  // Ensure bookDTO is passed
    }


    @Test
    void testConsume_ShouldHandleExceptionGracefully() throws Exception {
        String malformedMessage = "invalidMessage";

        when(objectMapper.readValue(malformedMessage, KafkaBookMessage.class)).thenThrow(new RuntimeException("Parsing error"));

        messageConsumer.consume(malformedMessage);

        verify(commandService, never()).createRecord(any());
        verify(commandService, never()).deleteBookStatus(any());
    }
}
