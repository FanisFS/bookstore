package com.bookstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.dto.PurchaseDTO;
import com.bookstore.service.PurchaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

	private final PurchaseService purchaseService;

	// Get all purchases
	@GetMapping
	public ResponseEntity<List<PurchaseDTO>> getAllPurchases() {
		return ResponseEntity.ok(purchaseService.getAllPurchases());
	}

	// Get a purchase by ID
	@GetMapping("/{id}")
	public ResponseEntity<PurchaseDTO> getPurchaseById(@PathVariable String id) {
		try {
			Long purchaseId = Long.parseLong(id);
			if (purchaseId < 0) {
				throw new IllegalArgumentException("Purchase ID must be greater than or equal to 0");
			}
			return ResponseEntity.ok(purchaseService.getPurchaseById(purchaseId));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid purchase ID format: " + id);
		}
	}

	// Create a new purchase
	@PostMapping
	public ResponseEntity<PurchaseDTO> createPurchase(@Valid @RequestBody PurchaseDTO purchaseDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.createPurchase(purchaseDTO));
	}

	// Update an existing purchase
	@PutMapping("/{id}")
	public ResponseEntity<PurchaseDTO> updatePurchase(@PathVariable String id, @Valid @RequestBody PurchaseDTO purchaseDTO) {
		try {
			Long purchaseId = Long.parseLong(id);
			if (purchaseId < 0) {
				throw new IllegalArgumentException("Purchase ID must be greater than or equal to 0");
			}
			return ResponseEntity.ok(purchaseService.updatePurchase(purchaseId, purchaseDTO));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid purchase ID format: " + id);
		}
	}

	// Delete a purchase by ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePurchase(@PathVariable String id) {
		try {
			Long purchaseId = Long.parseLong(id);
			if (purchaseId < 0) {
				throw new IllegalArgumentException("Purchase ID must be greater than or equal to 0");
			}
			purchaseService.deletePurchase(purchaseId);
			return ResponseEntity.noContent().build();
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid purchase ID format: " + id);
		}
	}
}
