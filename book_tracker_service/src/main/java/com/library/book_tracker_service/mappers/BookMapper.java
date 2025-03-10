package com.library.book_tracker_service.mappers;

import com.library.book_tracker_service.DTO.BookDTO;
import com.library.book_tracker_service.models.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookDTO bookToBookDTO(Book book);
    Book bookDTOToBook(BookDTO bookDTO);

    void updateBookDTO(BookDTO bookDTO, @MappingTarget Book book);
}
