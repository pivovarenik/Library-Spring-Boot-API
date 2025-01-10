package com.library.book_tracker_service.services.models;

public class KafkaMessage {
    private Long id;
    private Command command;

    public Long getId() {
        return id;
    }

    public Command getCommand() {
        return command;
    }
}
