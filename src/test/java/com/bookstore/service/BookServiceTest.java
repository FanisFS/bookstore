package com.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bookstore.dto.BookDTO;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.model.Book;
import com.bookstore.model.BookType;
import com.bookstore.repository.BookRepository;

class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BookMapper bookMapper;

	@InjectMocks
	private BookService bookService;

	private BookDTO bookDTO;
	private Book book;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		book = new Book(1L, "Dungeons & Dragons 5th edition", "Rodney Thompson", 39.99, BookType.REGULAR);
		bookDTO = new BookDTO(1L, "Dungeons & Dragons 5th edition", "Rodney Thompson", 39.99, BookType.REGULAR);
	}

	// ===========================
	// Test getAllBooks()
	// ===========================
	@Test
	public void testGetAllBooks() {
		given(bookRepository.findAll()).willReturn(Arrays.asList(book));
		given(bookMapper.convertToBookDTOList(any())).willReturn(Collections.singletonList(bookDTO));

		List<BookDTO> books = bookService.getAllBooks();

		assertEquals(1, books.size());
		assertEquals("Dungeons & Dragons 5th edition", books.get(0).getTitle());
		assertEquals("Rodney Thompson", books.get(0).getAuthor());
		assertEquals(39.99, books.get(0).getPrice());
		assertEquals(BookType.REGULAR, books.get(0).getType());

		verify(bookRepository).findAll();
	}

	@Test
	public void testGetAllBooks_whenNoBooksExist() {
		given(bookRepository.findAll()).willReturn(Collections.emptyList());

		List<BookDTO> books = bookService.getAllBooks();

		assertTrue(books.isEmpty());
		verify(bookRepository).findAll();
	}

	// ===========================
	// Test getBookById(Long)
	// ===========================
	@Test
	public void testGetBookById_whenBookExists() {
		given(bookRepository.findById(1L)).willReturn(Optional.of(book));
		given(bookMapper.convertToBookDTO(book)).willReturn(bookDTO);

		BookDTO result = bookService.getBookById(1L);

		assertEquals("Dungeons & Dragons 5th edition", result.getTitle());
		assertEquals("Rodney Thompson", result.getAuthor());
		assertEquals(39.99, result.getPrice());
		assertEquals(BookType.REGULAR, result.getType());

		verify(bookRepository).findById(1L);
	}

	@Test
	public void testGetBookById_whenBookDoesNotExist() {
		given(bookRepository.findById(2L)).willReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> {
			bookService.getBookById(2L);
		});

		verify(bookRepository).findById(2L);
	}

	// ===========================
	// Test createBook(BookDTO)
	// ===========================
	@Test
	public void testCreateBook() {
		given(bookMapper.convertToBook(bookDTO)).willReturn(book);
		given(bookRepository.save(book)).willReturn(book);
		given(bookMapper.convertToBookDTO(book)).willReturn(bookDTO);

		BookDTO result = bookService.createBook(bookDTO);

		assertEquals("Dungeons & Dragons 5th edition", result.getTitle());
		assertEquals("Rodney Thompson", result.getAuthor());
		assertEquals(39.99, result.getPrice());
		assertEquals(BookType.REGULAR, result.getType());

		verify(bookRepository).save(book);
	}

	// ===========================
	// Test updateBook(Long, BookDTO)
	// ===========================
	@Test
	public void testUpdateBook() {
		Book updatedBook = new Book(1L, "Dungeons & Dragons 5th edition", "Rodney Thompson", 29.99, BookType.OLD_EDITION);
		BookDTO updatedBookDTO = new BookDTO(1L, "Dungeons & Dragons 5th edition", "Rodney Thompson", 29.99, BookType.OLD_EDITION);

		given(bookRepository.findById(1L)).willReturn(Optional.of(updatedBook));
		given(bookMapper.convertToBook(updatedBookDTO)).willReturn(updatedBook);
		given(bookRepository.save(updatedBook)).willReturn(updatedBook);
		given(bookMapper.convertToBookDTO(updatedBook)).willReturn(updatedBookDTO);


		BookDTO result = bookService.updateBook(1L, updatedBookDTO);

		assertEquals(1L, result.getId());
		assertEquals("Dungeons & Dragons 5th edition", result.getTitle());
		assertEquals("Rodney Thompson", result.getAuthor());
		assertEquals(29.99, result.getPrice());
		assertEquals(BookType.OLD_EDITION, result.getType());

		verify(bookRepository).findById(1L);
		verify(bookRepository).save(updatedBook);
	}

	// ===========================
	// Test deleteBook(Long)
	// ===========================
	@Test
	public void testDeleteBook() {
		given(bookRepository.findById(1L)).willReturn(Optional.of(book));

		boolean result = bookService.deleteBook(1L);

		assertTrue(result);
		verify(bookRepository).findById(1L);
		verify(bookRepository).delete(book);
	}

	@Test
	public void testDeleteBook_whenBookDoesNotExist() {
		given(bookRepository.findById(2L)).willReturn(Optional.empty());

		// When: Call deleteBook
		assertThrows(BookNotFoundException.class, () -> {
			bookService.deleteBook(2L);
		});

		verify(bookRepository).findById(2L);
	}
}
