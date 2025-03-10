package com.library.book_storage_service.DTO;

import com.library.book_storage_service.models.enums.Role;

import java.util.Objects;

public record UserDTO(
        String username,
        Role role
    ){}