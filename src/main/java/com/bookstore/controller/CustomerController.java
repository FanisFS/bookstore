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

import com.bookstore.dto.CustomerDTO;
import com.bookstore.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	// Get all customers
	@GetMapping
	public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
		return ResponseEntity.ok(customerService.getAllCustomers());
	}

	// Get a customer by ID
	@GetMapping("/{id}")
	public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable String id) {
		try {
			Long customerId = Long.parseLong(id);
			if (customerId < 0) {
				throw new IllegalArgumentException("Customer ID must be greater than or equal to 0");
			}
			return ResponseEntity.ok(customerService.getCustomerById(customerId));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid customer ID format: " + id);
		}
	}

	// Create a new customer
	@PostMapping
	public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
		return ResponseEntity.status(201).body(customerService.createCustomer(customerDTO));
	}

	// Update an existing customer
	@PutMapping("/{id}")
	public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable String id, @Valid @RequestBody CustomerDTO customerDTO) {
		try {
			Long customerId = Long.parseLong(id);
			if (customerId < 0) {
				throw new IllegalArgumentException("Customer ID must be greater than or equal to 0");
			}
			return ResponseEntity.ok(customerService.updateCustomer(customerId, customerDTO));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid customer ID format: " + id);
		}
	}

	// Delete a customer by ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
		try {
			Long customerId = Long.parseLong(id);
			if (customerId < 0) {
				throw new IllegalArgumentException("Customer ID must be greater than or equal to 0");
			}
			customerService.deleteCustomer(customerId);
			return ResponseEntity.noContent().build();
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid customer ID format: " + id);
		}
	}
}