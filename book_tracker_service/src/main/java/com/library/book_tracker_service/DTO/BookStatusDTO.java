package com.library.book_tracker_service.DTO;


import java.time.LocalDateTime;

public record BookStatusDTO(
        String status,
        LocalDateTime borrowedAt,
        LocalDateTime dueAt
) {
}