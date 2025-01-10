package com.library.book_storage_service.services;

import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.repos.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.stereotype.Service;

@Service
public class CommandService {
    private final BookRepository bookRepository;
    private final MessageProducer messageProducer;

    public CommandService(BookRepository bookRepository, MessageProducer messageProducer) {
        this.bookRepository = bookRepository;
        this.messageProducer = messageProducer;
    }

    public Book createBook(Book book) {
        Book book1 = bookRepository.save(book);
        messageProducer.sendMessage("books",String.format("{\"id\": %d, \"command\": \"ADD\"}", book.getId()));
        return book1;
    }

    public void updateBook(Long id, Book updatedBook) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setGenre(updatedBook.getGenre());
        existingBook.setDescription(updatedBook.getDescription());
        bookRepository.save(existingBook);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
        messageProducer.sendMessage("books",String.format("{\"id\": %d, \"command\": \"DELETE\"}",id));
    }
}
