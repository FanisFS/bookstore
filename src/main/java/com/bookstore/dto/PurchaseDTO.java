package com.bookstore.dto;

import java.util.List;

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
public class PurchaseDTO {
	private Long id;

	@NotNull(message = "Customer ID cannot be null")
	private Long customerId;

	@NotNull(message = "Book IDs cannot be null")
	private List<Long> bookIds;

	@Min(value = 0, message = "Total price must be at least 0")
	private double totalPrice;
}