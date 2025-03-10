package com.library.book_storage_service.mappers;


import com.library.book_storage_service.DTO.BookDTO;
import com.library.book_storage_service.DTO.UserDTO;
import com.library.book_storage_service.models.Book;
import com.library.book_storage_service.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO userDTO);
    List<UserDTO> usersToUserDTOs(List<User> users);
}
