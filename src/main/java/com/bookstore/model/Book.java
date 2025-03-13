package com.bookstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Book title cannot be null")
	@Column(nullable = false, length = 255)
	private String title;

	@NotNull(message = "Author cannot be null")
	@Column(nullable = false, length = 255)
	private String author;

	@Min(value = 0, message = "Price must be greater than or equal to 0")
	@Column(nullable = false)
	private double price;

	@NotNull(message = "Book type cannot be null")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookType type;
}