package com.bookstore.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.bookstore.dto.BookDTO;
import com.bookstore.model.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

	BookDTO			convertToBookDTO		(Book book);
	List<BookDTO>	convertToBookDTOList	(List<Book> books);

	Book			convertToBook			(BookDTO bookDTO);

}