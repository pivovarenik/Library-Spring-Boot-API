package com.library.book_tracker_service.services.models;

import com.library.book_tracker_service.models.Book;
import com.library.book_tracker_service.services.models.Command;

public class KafkaBookMessage {
    private Command command;
    private Long id;
    private Book book;

    public KafkaBookMessage() {
    }

    public KafkaBookMessage(Command command, Long id, Book book) {
        this.command = command;
        this.id = id;
        this.book = book;
    }

    public KafkaBookMessage(Command command, Book book) {
        this.command = command;
        this.id = book.getId();
        this.book = book;
    }

    public KafkaBookMessage(Command command, Long id) {
        this.command = command;
        this.id = id;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
