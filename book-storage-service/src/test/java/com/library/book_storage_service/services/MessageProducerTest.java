package com.library.book_storage_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class MessageProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MessageProducer messageProducer;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testSendMessage_Success() throws JsonProcessingException {
        String topic = "test-topic";
        TestMessage message = new TestMessage("Hello, Kafka!");
        String jsonMessage = "{\"message\":\"Hello, Kafka!\"}";

        when(objectMapper.writeValueAsString(message)).thenReturn(jsonMessage);

        messageProducer.sendMessage(topic, message);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(1)).send(eq(topic), captor.capture());

        assertEquals(jsonMessage, captor.getValue());
    }

    @Test
    void testSendMessage_JsonProcessingException() throws JsonProcessingException {
        String topic = "test-topic";
        TestMessage message = new TestMessage("Hello, Kafka!");

        when(objectMapper.writeValueAsString(message)).thenThrow(new JsonProcessingException("Error") {});

        assertDoesNotThrow(() -> messageProducer.sendMessage(topic, message));

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    private static class TestMessage {
        public String message;

        public TestMessage(String message) {
            this.message = message;
        }
    }
}
