package com.bookstore.dto;

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
public class CustomerDTO {
	private Long id;

	@NotNull(message = "Customer name cannot be null")
	private String name;

	@Min(value = 0, message = "Minimum loyalty points must be 0")
	private int loyaltyPoints;

}