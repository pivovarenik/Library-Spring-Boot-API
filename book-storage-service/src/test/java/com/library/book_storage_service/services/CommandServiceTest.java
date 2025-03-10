package com.library.book_storage_service.services;

import com.library.book_storage_service.DTO.BookDTO;
import com.library.book_storage_service.mappers.BookMapper;
import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.models.KafkaBookMessage;
import com.library.book_storage_service.repos.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CommandServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MessageProducer messageProducer;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CommandService commandService;

    private BookDTO bookDTO;
    private Book book;

    @BeforeEach
    void setUp() {
        try(AutoCloseable au = openMocks(this)){

            bookDTO = new BookDTO("1232313123", "Book Title", "Author", "12345","description");
            book = new Book(1L, "1232313123", "Book Title", "Author", "12345","description");

            when(bookMapper.bookDTOToBook(bookDTO)).thenReturn(book);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateBook_Success() {

        when(bookRepository.save(any(Book.class))).thenReturn(book);
        boolean result = commandService.createBook(bookDTO);
        assertTrue(result);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(messageProducer, times(1)).sendMessage(eq("books"), any(KafkaBookMessage.class));
    }

    @Test
    void testCreateBook_Failure() {

        when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Error"));

        boolean result = commandService.createBook(bookDTO);

        assertFalse(result);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(messageProducer, times(0)).sendMessage(eq("books"), any(KafkaBookMessage.class));
    }

    @Test
    void testUpdateBook_Success() {
        Book existingBook = new Book(1L,"123123", "Old Title", "fiction", "12345","Old Author");
        BookDTO updatedBookDTO = new BookDTO("1242-123", "New Title", "New Author", "marvel","boring");
        when(bookRepository.findById(1L)).thenReturn(java.util.Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        commandService.updateBook(1L, updatedBookDTO);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(existingBook);
        verify(messageProducer, times(1)).sendMessage(eq("books"), any(KafkaBookMessage.class));
    }

    @Test
    void testUpdateBook_Failure_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commandService.updateBook(1L, bookDTO));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(0)).save(any(Book.class));
        verify(messageProducer, times(0)).sendMessage(eq("books"), any(KafkaBookMessage.class));
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        commandService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
        verify(messageProducer, times(1)).sendMessage(eq("books"), any(KafkaBookMessage.class));
    }

    @Test
    void testDeleteBook_Failure() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        commandService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
        verify(messageProducer, times(1)).sendMessage(eq("books"), any(KafkaBookMessage.class));
    }
}
