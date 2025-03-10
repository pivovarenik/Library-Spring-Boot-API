package com.library.book_storage_service.services;

import com.library.book_storage_service.DTO.BookDTO;
import com.library.book_storage_service.mappers.BookMapper;
import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.models.KafkaBookMessage;
import com.library.book_storage_service.repos.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.stereotype.Service;

import static com.library.book_storage_service.models.enums.Command.*;

@Service
public class CommandService {
    private final BookRepository bookRepository;
    private final MessageProducer messageProducer;
    private final BookMapper bookMapper;
    public CommandService(BookRepository bookRepository, MessageProducer messageProducer, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.messageProducer = messageProducer;
        this.bookMapper = bookMapper;
    }

    public boolean createBook(BookDTO book) {
        try{
            Book book1 = bookRepository.save(bookMapper.bookDTOToBook(book));
            messageProducer.sendMessage("books", new KafkaBookMessage(ADD, book1));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void updateBook(Long id, BookDTO updatedBook) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        bookMapper.updateBookDTO(updatedBook, existingBook);
        messageProducer.sendMessage("books", new KafkaBookMessage(UPDATE, existingBook));
        bookRepository.save(existingBook);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
        messageProducer.sendMessage("books", new KafkaBookMessage(DELETE, id));
    }
}
