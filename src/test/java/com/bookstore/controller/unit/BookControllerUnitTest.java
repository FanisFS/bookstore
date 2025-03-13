package com.bookstore.controller.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bookstore.controller.BookController;
import com.bookstore.dto.BookDTO;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.GlobalExceptionHandler;
import com.bookstore.model.Book;
import com.bookstore.model.BookType;
import com.bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

class BookControllerUnitTest {

	@Mock
	private BookService bookService;

	@InjectMocks
	private BookController bookController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	private BookDTO bookDTO1, bookDTO2;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(bookController)
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
		objectMapper = new ObjectMapper();

		Book book1 = new Book(1L, "Dungeons & Dragons 5th edition", "Rodney Thompson", 39.99, BookType.REGULAR);
		Book book2 = new Book(2L, "The Hobbit", "J.R.R. Tolkien", 25.99, BookType.REGULAR);

		bookDTO1 = new BookDTO(book1.getId(), book1.getTitle(), book1.getAuthor(), book1.getPrice(), book1.getType());
		bookDTO2 = new BookDTO(book2.getId(), book2.getTitle(), book2.getAuthor(), book2.getPrice(), book2.getType());
	}

	// ===========================
	// Test getAllBooks()
	// ===========================

	@Test
	public void testGetAllBooks_ReturnsAllBooks() throws Exception {
		given(bookService.getAllBooks()).willReturn(List.of(bookDTO1, bookDTO2));

		mockMvc.perform(get("/api/books"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	public void testGetAllBooks_EmptyDatabase() throws Exception {
		given(bookService.getAllBooks()).willReturn(List.of());

		mockMvc.perform(get("/api/books"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").isEmpty());
	}

	// ===========================
	// Test getBookById(Long)
	// ===========================

	@Test
	public void testGetBookById_ReturnsBook() throws Exception {
		given(bookService.getBookById(bookDTO1.getId())).willReturn(bookDTO1);

		mockMvc.perform(get("/api/books/" + bookDTO1.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title").value(bookDTO1.getTitle()));
	}

	@Test
	public void testGetBookById_NotFound() throws Exception {
		given(bookService.getBookById(100L)).willThrow(new BookNotFoundException("Book with ID 100 not found"));

		mockMvc.perform(get("/api/books/100"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.message").value("Book with ID 100 not found"));
	}

	@Test
	public void testGetBookById_NegativeId() throws Exception {
		mockMvc.perform(get("/api/books/-1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Book ID must be greater than or equal to 0"));
	}

	@Test
	public void testGetBookById_InvalidIdFormat() throws Exception {
		mockMvc.perform(get("/api/books/abc"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Invalid book ID format: abc"));
	}

	// ===========================
	// Test createBook(BookDTO)
	// ===========================

	@Test
	public void testCreateBook_CreatesBookSuccessfully() throws Exception {
		given(bookService.createBook(any(BookDTO.class))).willReturn(bookDTO1);

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookDTO1)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.title").value(bookDTO1.getTitle()));
	}

	@Test
	public void testCreateBook_InvalidBookType() throws Exception {
		BookDTO newBook = new BookDTO(null, "The Silmarillion", "J.R.R. Tolkien", 49.99, null);

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newBook)))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void testCreateBook_InvalidPrice() throws Exception {
		BookDTO newBook = new BookDTO(null, "The Silmarillion", "J.R.R. Tolkien", -49.99, BookType.REGULAR);

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newBook)))
		.andExpect(status().isBadRequest());
	}

	// ===========================
	// Test updateBook(Long, BookDTO)
	// ===========================

	@Test
	public void testUpdateBook_SuccessfulUpdate() throws Exception {
		given(bookService.updateBook(bookDTO1.getId(), bookDTO1)).willReturn(bookDTO1);

		mockMvc.perform(put("/api/books/" + bookDTO1.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookDTO1)))
		.andExpect(status().isOk());
	}

	@Test
	public void testUpdateBook_NotFound() throws Exception {
		given(bookService.updateBook(any(), any(BookDTO.class))).willThrow(new BookNotFoundException("Book with ID 100 not found"));

		mockMvc.perform(put("/api/books/100")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookDTO1)))
		.andExpect(status().isNotFound());
	}

	@Test
	public void testUpdateBook_InvalidIdFormat() throws Exception {
		mockMvc.perform(put("/api/books/abc")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookDTO1)))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdateBook_InvalidPrice() throws Exception {
		BookDTO updatedBook = new BookDTO(bookDTO1.getId(), "D&D 6th Edition", "Rodney Thompson", -5.00, BookType.REGULAR);

		mockMvc.perform(put("/api/books/" + bookDTO1.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedBook)))
		.andExpect(status().isBadRequest());
	}

	// ===========================
	// Test deleteBook(Long)
	// ===========================

	@Test
	public void testDeleteBook_DeletesBookSuccessfully() throws Exception {
		given(bookService.deleteBook(bookDTO2.getId())).willReturn(true);

		mockMvc.perform(delete("/api/books/" + bookDTO2.getId()))
		.andExpect(status().isNoContent());
	}

	@Test
	public void testDeleteBook_NotFound() throws Exception {
		given(bookService.deleteBook(100L)).willThrow(new BookNotFoundException("Book with ID 100 not found"));

		mockMvc.perform(delete("/api/books/100"))
		.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteBook_NegativeId() throws Exception {
		mockMvc.perform(delete("/api/books/-1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Book ID must be greater than or equal to 0"));
	}

	@Test
	public void testDeleteBook_InvalidIdFormat() throws Exception {
		mockMvc.perform(get("/api/books/abc"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Invalid book ID format: abc"));
	}
}
