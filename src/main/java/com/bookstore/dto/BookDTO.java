package com.bookstore.dto;

import com.bookstore.model.BookType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
	private Long id;

	@NotNull(message = "Title cannot be null")
	private String title;

	@NotNull(message = "Author cannot be null")
	private String author;

	@NotNull(message = "Price cannot be null")
	@Min(value = 1, message = "Price must be at least 1")
	private double price;

	@NotNull(message = "Book type cannot be null")
	private BookType type;
}