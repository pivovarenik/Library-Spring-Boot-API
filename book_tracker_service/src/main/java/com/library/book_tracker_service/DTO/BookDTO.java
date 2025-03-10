package com.library.book_tracker_service.DTO;

public record BookDTO(
        String isbn,
        String title,
        String author,
        String genre,
        String description
) {
}