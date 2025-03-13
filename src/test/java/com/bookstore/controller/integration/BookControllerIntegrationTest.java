package com.bookstore.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.bookstore.dto.BookDTO;
import com.bookstore.model.Book;
import com.bookstore.model.BookType;
import com.bookstore.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
class BookControllerIntegrationTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BookRepository bookRepository;

	private MockMvc mockMvc;

	private Book book1, book2;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		bookRepository.deleteAll();

		// Setup initial books
		book1 = bookRepository.save(new Book(null, "Dungeons & Dragons 5th edition", "Rodney Thompson", 39.99, BookType.REGULAR));
		book2 = bookRepository.save(new Book(null, "The Hobbit", "J.R.R. Tolkien", 25.99, BookType.REGULAR));
		bookRepository.save(new Book(null, "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", 29.99, BookType.REGULAR));
	}

	// ===========================
	// getAllBooks()
	// ===========================

	@Test
	public void testGetAllBooks_ReturnsAllBooks() throws Exception {
		mockMvc.perform(get("/api/books"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$.length()").value(3));
	}

	@Test
	public void testGetAllBooks_EmptyDatabase() throws Exception {
		bookRepository.deleteAll();

		mockMvc.perform(get("/api/books"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").isEmpty());
	}

	// ===========================
	// getBookById(Long)
	// ===========================

	@Test
	public void testGetBookById_ReturnsBook() throws Exception {
		mockMvc.perform(get("/api/books/" + book1.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title").value(book1.getTitle()));
	}

	@Test
	public void testGetBookById_NotFound() throws Exception {
		mockMvc.perform(get("/api/books/100"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.message").value("Book with ID 100 not found"));
	}


	@Test
	public void testGetBookById_NegativeId() throws Exception {
		mockMvc.perform(get("/api/books/-1"))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void testGetBookById_InvalidIdFormat() throws Exception {
		mockMvc.perform(get("/api/books/abc"))
		.andExpect(status().isBadRequest());
	}

	// ===========================
	// createBook(BookDTO)
	// ===========================

	@Test
	public void testCreateBook_CreatesBookSuccessfully() throws Exception {
		BookDTO newBook = new BookDTO(null, "The Lord of the Rings", "J.R.R. Tolkien", 59.99, BookType.REGULAR);

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newBook)))
		.andExpect(status().isCreated());
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
	public void testCreateBook_ZeroPrice() throws Exception {
		BookDTO newBook = new BookDTO(null, "Cheap Book", "Unknown", 0.00, BookType.REGULAR);

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newBook)))
		.andExpect(status().isBadRequest());
	}

	// ===========================
	// updateBook(Long, BookDTO)
	// ===========================

	@Test
	public void testUpdateBook_UpdatesBookSuccessfully() throws Exception {
		BookDTO updatedBookDTO = new BookDTO(book1.getId(), "D&D 6th Edition", "Rodney Thompson", 49.99, BookType.REGULAR);

		mockMvc.perform(put("/api/books/" + book1.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedBookDTO)))
		.andExpect(status().isOk());
	}

	@Test
	public void testUpdateBook_NotFound() throws Exception {
		BookDTO updatedBookDTO = new BookDTO(100L, "Ghost Book", "Unknown", 19.99, BookType.REGULAR);

		mockMvc.perform(put("/api/books/100")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedBookDTO)))
		.andExpect(status().isNotFound());
	}

	@Test
	public void testUpdateBook_InvalidPrice() throws Exception {
		BookDTO updatedBookDTO = new BookDTO(book1.getId(), "D&D 6th Edition", "Rodney Thompson", -5.00, BookType.REGULAR);

		mockMvc.perform(put("/api/books/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedBookDTO)))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdateBook_NegativeId() throws Exception {
		BookDTO updatedBookDTO = new BookDTO(book1.getId(), "D&D 6th Edition", "Rodney Thompson", 49.99, BookType.REGULAR);

		mockMvc.perform(put("/api/books/-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedBookDTO)))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdateBook_NonNumericId() throws Exception {
		BookDTO updatedBookDTO = new BookDTO(book1.getId(), "D&D 6th Edition", "Rodney Thompson", 49.99, BookType.REGULAR);

		mockMvc.perform(put("/api/books/abc")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedBookDTO)))
		.andExpect(status().isBadRequest());
	}

	// ===========================
	// deleteBook(Long)
	// ===========================

	@Test
	public void testDeleteBook_DeletesBookSuccessfully() throws Exception {
		mockMvc.perform(delete("/api/books/" + book2.getId()))
		.andExpect(status().isNoContent());
	}

	@Test
	public void testDeleteBook_NotFound() throws Exception {
		mockMvc.perform(delete("/api/books/100"))
		.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteBook_NegativeId() throws Exception {
		mockMvc.perform(delete("/api/books/-1"))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void testDeleteBook_NonNumericId() throws Exception {
		mockMvc.perform(delete("/api/books/abc"))
		.andExpect(status().isBadRequest());
	}
}