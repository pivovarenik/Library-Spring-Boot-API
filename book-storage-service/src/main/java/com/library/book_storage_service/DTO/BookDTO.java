package com.library.book_storage_service.DTO;

import java.util.Objects;

public record BookDTO(
        String isbn,
        String title,
        String author,
        String genre,
        String description
) {
}