package com.library.book_tracker_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.book_tracker_service.controllers.BookStatusController;
import com.library.book_tracker_service.mappers.BookMapper;
import com.library.book_tracker_service.services.models.KafkaBookMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    private final ObjectMapper objectMapper;
    private final QueryService queryService;
    private final CommandService commandService;
    private final BookMapper bookMapper;

    public MessageConsumer(ObjectMapper objectMapper,QueryService queryService, CommandService commandService, BookMapper bookMapper) {
        this.objectMapper = objectMapper;
        this.queryService = queryService;
        this.commandService = commandService;
        this.bookMapper = bookMapper;
    }
    @KafkaListener(topics = "books", groupId = "group_id")
    public void consume(String message) {
        try{
            KafkaBookMessage kafkaMessage = objectMapper.readValue(message, KafkaBookMessage.class);
            switch (kafkaMessage.getCommand()) {
                case ADD -> {
                    commandService.createRecord(kafkaMessage.getId());
                    commandService.addBook(bookMapper.bookToBookDTO(kafkaMessage.getBook()));
                }
                case DELETE -> {
                    commandService.deleteBookStatus(kafkaMessage.getId());
                    commandService.deleteBook(kafkaMessage.getId());
                }
                case UPDATE -> {
                    commandService.updateBook(kafkaMessage.getId(),bookMapper.bookToBookDTO(kafkaMessage.getBook()));
                }
                default ->  System.out.println("Unknown command: " + kafkaMessage.getCommand());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
