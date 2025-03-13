package com.bookstore.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.dto.BookDTO;
import com.bookstore.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

	private final BookService bookService;

	// Get all books
	@GetMapping
	public ResponseEntity<List<BookDTO>> getAllBooks() {
		return ResponseEntity.ok(bookService.getAllBooks());
	}

	// Get a book by ID
	@GetMapping("/{id}")
	public ResponseEntity<BookDTO> getBookById(@PathVariable String id) {
		try {
			Long bookId = Long.parseLong(id);
			if (bookId < 0) {
				throw new IllegalArgumentException("Book ID must be greater than or equal to 0");
			}
			return ResponseEntity.ok(bookService.getBookById(bookId));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid book ID format: " + id);
		}
	}

	// Create a new book
	@PostMapping
	public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
		return ResponseEntity.status(201).body(bookService.createBook(bookDTO));
	}

	// Update an existing book
	@PutMapping("/{id}")
	public ResponseEntity<BookDTO> updateBook(@PathVariable String id, @Valid @RequestBody BookDTO bookDTO) {
		try {
			Long bookId = Long.parseLong(id);
			if (bookId < 0) {
				throw new IllegalArgumentException("Book ID must be greater than or equal to 0");
			}
			return ResponseEntity.ok(bookService.updateBook(bookId, bookDTO));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid book ID format: " + id);
		}
	}

	// Delete a book by ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(@PathVariable String id) {
		try {
			Long bookId = Long.parseLong(id);
			if (bookId < 0) {
				throw new IllegalArgumentException("Book ID must be greater than or equal to 0");
			}
			bookService.deleteBook(bookId);
			return ResponseEntity.noContent().build();
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid book ID format: " + id);
		}
	}
}