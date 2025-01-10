package com.library.book_storage_service;

import com.library.book_storage_service.services.MessageProducer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class MessageProducerTest {

    @Test
    void sendMessage_ShouldCallKafkaTemplateWithCorrectParameters() {
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        MessageProducer messageProducer = new MessageProducer(kafkaTemplate);

        String topic = "test-topic";
        String message = "test-message";

        messageProducer.sendMessage(topic, message);


        verify(kafkaTemplate, times(1)).send(topic, message);
    }
}
