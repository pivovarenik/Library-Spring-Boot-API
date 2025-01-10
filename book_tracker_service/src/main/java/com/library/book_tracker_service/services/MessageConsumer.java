package com.library.book_tracker_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.book_tracker_service.controllers.BookStatusController;
import com.library.book_tracker_service.services.models.KafkaMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QueryService queryService;
    private final CommandService commandService;

    public MessageConsumer(QueryService queryService, CommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }
    @KafkaListener(topics = "books", groupId = "group_id")
    public void consume(String message) {
        try{
            KafkaMessage kafkaMessage = objectMapper.readValue(message, KafkaMessage.class);
            switch (kafkaMessage.getCommand()) {
                case ADD -> commandService.createRecord(kafkaMessage.getId());
                case DELETE -> commandService.deleteBookStatus(kafkaMessage.getId());
                default -> {
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
