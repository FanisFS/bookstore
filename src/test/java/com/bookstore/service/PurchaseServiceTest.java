package com.bookstore.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bookstore.dto.PurchaseDTO;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.CustomerNotFoundException;
import com.bookstore.exception.PurchaseNotFoundException;
import com.bookstore.mapper.PurchaseMapper;
import com.bookstore.model.Book;
import com.bookstore.model.BookType;
import com.bookstore.model.Customer;
import com.bookstore.model.Purchase;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CustomerRepository;
import com.bookstore.repository.PurchaseRepository;

class PurchaseServiceTest {

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private PurchaseMapper purchaseMapper;

	@InjectMocks
	private PurchaseService purchaseService;

	private PurchaseDTO purchaseDTO;
	private Purchase purchase;
	private Customer customer;
	private Book book;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		// Setup entities for testing
		customer = new Customer(1L, "John Doe", 5);
		book = new Book(1L, "Dungeons & Dragons 5th edition", "Rodney Thompson", 39.99, BookType.REGULAR);
		purchase = new Purchase(1L, customer, Arrays.asList(book), 39.99, false);
		purchaseDTO = new PurchaseDTO(1L, 1L, Arrays.asList(1L), 39.99);
	}

	// ===========================
	// Test getAllPurchases()
	// ===========================
	@Test
	public void testGetAllPurchases() {
		given(purchaseRepository.findAll()).willReturn(Arrays.asList(purchase));
		given(purchaseMapper.convertToPurchaseDTOList(any())).willReturn(Collections.singletonList(purchaseDTO));

		List<PurchaseDTO> purchases = purchaseService.getAllPurchases();

		assertEquals(1, purchases.size());
		assertEquals(1L, purchases.get(0).getCustomerId());
		assertEquals(1L, purchases.get(0).getBookIds().get(0));
		assertEquals(39.99, purchases.get(0).getTotalPrice());

		verify(purchaseRepository).findAll();
	}

	@Test
	public void testGetAllPurchases_whenNoPurchasesExist() {
		given(purchaseRepository.findAll()).willReturn(Collections.emptyList());

		List<PurchaseDTO> purchases = purchaseService.getAllPurchases();

		assertTrue(purchases.isEmpty());
		verify(purchaseRepository).findAll();
	}

	// ===========================
	// Test getPurchaseById(Long)
	// ===========================
	@Test
	public void testGetPurchaseById_whenPurchaseExists() {
		given(purchaseRepository.findById(1L)).willReturn(Optional.of(purchase));
		given(purchaseMapper.convertToPurchaseDTO(purchase)).willReturn(purchaseDTO);

		PurchaseDTO result = purchaseService.getPurchaseById(1L);

		assertEquals(1L, result.getId());
		assertEquals(1L, result.getCustomerId());
		assertEquals(39.99, result.getTotalPrice());

		verify(purchaseRepository).findById(1L);
	}

	@Test
	public void testGetPurchaseById_whenPurchaseDoesNotExist() {
		given(purchaseRepository.findById(2L)).willReturn(Optional.empty());

		assertThrows(PurchaseNotFoundException.class, () -> {
			purchaseService.getPurchaseById(2L);
		});

		verify(purchaseRepository).findById(2L);
	}

	// ===========================
	// Test createPurchase(PurchaseDTO)
	// ===========================
	@Test
	public void testCreatePurchase() {
		given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
		given(bookRepository.findAllById(Arrays.asList(1L))).willReturn(Arrays.asList(book));
		given(purchaseMapper.convertToPurchase(any(PurchaseDTO.class))).willReturn(purchase);
		given(purchaseRepository.save(any(Purchase.class))).willReturn(purchase);
		given(purchaseMapper.convertToPurchaseDTO(any(Purchase.class))).willReturn(purchaseDTO);

		PurchaseDTO result = purchaseService.createPurchase(purchaseDTO);

		assertEquals(1L, result.getId());
		assertEquals(1L, result.getCustomerId());
		assertEquals(39.99, result.getTotalPrice());

		verify(purchaseRepository).save(any(Purchase.class));
		verify(customerRepository).findById(1L);
		verify(bookRepository).findAllById(Arrays.asList(1L));
	}

	@Test
	public void testCreatePurchase_CustomerNotFound() {
		given(customerRepository.findById(1L)).willReturn(Optional.empty());

		assertThrows(CustomerNotFoundException.class, () -> {
			purchaseService.createPurchase(purchaseDTO);
		});

		verify(customerRepository).findById(1L);
		verify(bookRepository, never()).findAllById(any());
		verify(purchaseRepository, never()).save(any());
	}

	@Test
	public void testCreatePurchase_BookNotFound() {
		given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
		given(bookRepository.findAllById(Arrays.asList(1L))).willReturn(Arrays.asList()); // Simulate book not found

		assertThrows(BookNotFoundException.class, () -> {
			purchaseService.createPurchase(purchaseDTO);
		});

		verify(customerRepository).findById(1L);
		verify(bookRepository).findAllById(Arrays.asList(1L));
		verify(purchaseRepository, never()).save(any());
	}

	// ===========================
	// Test updatePurchase(Long, PurchaseDTO)
	// ===========================
	@Test
	public void testUpdatePurchase() {
		Purchase updatedPurchase = new Purchase(1L, customer, Arrays.asList(book), 29.99, true);
		PurchaseDTO updatedPurchaseDTO = new PurchaseDTO(1L, 1L, Arrays.asList(1L), 29.99);

		given(purchaseRepository.findById(1L)).willReturn(Optional.of(updatedPurchase));
		given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
		given(bookRepository.findAllById(Arrays.asList(1L))).willReturn(Arrays.asList(book));
		given(purchaseMapper.convertToPurchase(updatedPurchaseDTO)).willReturn(updatedPurchase);
		given(purchaseRepository.save(updatedPurchase)).willReturn(updatedPurchase);
		given(purchaseMapper.convertToPurchaseDTO(updatedPurchase)).willReturn(updatedPurchaseDTO);

		PurchaseDTO result = purchaseService.updatePurchase(1L, updatedPurchaseDTO);

		assertEquals(1L, result.getId());
		assertEquals(29.99, result.getTotalPrice());

		verify(purchaseRepository).findById(1L);
		verify(purchaseRepository).save(updatedPurchase);
	}

	// ===========================
	// Test deletePurchase(Long)
	// ===========================
	@Test
	public void testDeletePurchase() {
		given(purchaseRepository.findById(1L)).willReturn(Optional.of(purchase));

		boolean result = purchaseService.deletePurchase(1L);

		assertTrue(result);
		verify(purchaseRepository).findById(1L);
		verify(purchaseRepository).delete(purchase);
	}

	@Test
	public void testDeletePurchase_whenPurchaseDoesNotExist() {
		given(purchaseRepository.findById(2L)).willReturn(Optional.empty());

		assertThrows(PurchaseNotFoundException.class, () -> {
			purchaseService.deletePurchase(2L);
		});

		verify(purchaseRepository, never()).deleteById(any());
	}
}
