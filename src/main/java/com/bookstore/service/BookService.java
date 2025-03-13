package com.bookstore.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.dto.BookDTO;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;
	private final BookMapper bookMapper;

	@Transactional(readOnly = true)
	public List<BookDTO> getAllBooks() {
		return bookMapper.convertToBookDTOList(bookRepository.findAll());
	}

	@Transactional(readOnly = true)
	public BookDTO getBookById(Long id) {
		Book book = getBook(id);
		return bookMapper.convertToBookDTO(book);
	}

	@Transactional
	public BookDTO createBook(BookDTO bookDTO) {
		Book book = bookMapper.convertToBook(bookDTO);
		return bookMapper.convertToBookDTO(bookRepository.save(book));
	}

	@Transactional
	public BookDTO updateBook(Long id, BookDTO bookDTO) {
		Book book = getBook(id);

		book.setTitle(bookDTO.getTitle());
		book.setAuthor(bookDTO.getAuthor());
		book.setPrice(bookDTO.getPrice());
		book.setType(bookDTO.getType());

		return bookMapper.convertToBookDTO(bookRepository.save(book));
	}

	@Transactional
	public boolean deleteBook(Long id) {
		Book book = getBook(id);
		bookRepository.delete(book);
		return true;
	}
	
	private Book getBook(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));
	}
}