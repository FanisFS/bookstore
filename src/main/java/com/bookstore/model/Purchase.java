package com.bookstore.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToMany
	@JoinTable(
			name = "purchase_books",
			joinColumns = @JoinColumn(name = "purchase_id"),
			inverseJoinColumns = @JoinColumn(name = "book_id")
			)
	private List<Book> books;

	@Column(nullable = false)
	private double totalPrice;

	@Column(nullable = false)
	private boolean loyaltyPointsUsed = false;

	public Purchase(Customer customer, List<Book> books, double totalPrice, boolean loyaltyPointsUsed) {
		this.customer			= customer;
		this.books				= books;
		this.totalPrice			= totalPrice;
		this.loyaltyPointsUsed	= loyaltyPointsUsed;
	}
}