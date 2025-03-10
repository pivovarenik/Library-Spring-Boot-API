package com.library.book_tracker_service.mappers;

import com.library.book_tracker_service.DTO.BookStatusDTO;
import com.library.book_tracker_service.models.BookStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookStatusMapper {
    BookStatusMapper bookStatusMapper = Mappers.getMapper(BookStatusMapper.class);

    BookStatus bookStatusDTOtoBookStatus(BookStatusDTO bookStatusDTO);
    BookStatusDTO bookStatusToBookStatusDTO(BookStatus bookStatus);
}
