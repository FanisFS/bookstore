package com.bookstore.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.dto.PurchaseDTO;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.CustomerNotFoundException;
import com.bookstore.exception.InvalidBookTypeException;
import com.bookstore.exception.PurchaseNotFoundException;
import com.bookstore.mapper.PurchaseMapper;
import com.bookstore.model.Book;
import com.bookstore.model.BookType;
import com.bookstore.model.Customer;
import com.bookstore.model.Purchase;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CustomerRepository;
import com.bookstore.repository.PurchaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseService {

	private final PurchaseRepository purchaseRepository;
	private final CustomerRepository customerRepository;
	private final BookRepository bookRepository;
	private final PurchaseMapper purchaseMapper;

	@Transactional(readOnly = true)
	public List<PurchaseDTO> getAllPurchases() {
		return purchaseMapper.convertToPurchaseDTOList(purchaseRepository.findAll());
	}

	@Transactional(readOnly = true)
	public PurchaseDTO getPurchaseById(Long id) {
		Purchase purchase = purchaseRepository.findById(id)
				.orElseThrow(() -> new PurchaseNotFoundException("Purchase not found with ID: " + id));
		return purchaseMapper.convertToPurchaseDTO(purchase);
	}

	private double calculateBookPrice(Book book, int bookCount) {
		double bookPrice = book.getPrice();

		switch (book.getType()) {
		case	OLD_EDITION	: {
			// 20% discount
			bookPrice *= 0.8;

			// Additional 5% discount
			if (bookCount >= 3) {
				bookPrice *= 0.95;
			}
			break;
		}
		case	REGULAR		: {
			// 10% discount
			if (bookCount >= 3) {
				bookPrice *= 0.9;
			}
			break;
		}
		case	NEW_RELEASE	: {
			// No discount applied
			break;
		}

		default				: {
			throw new InvalidBookTypeException("Unknown book type: " + book.getType());
		}
		}

		return bookPrice;
	}

	@Transactional
	public PurchaseDTO createPurchase(PurchaseDTO purchaseDTO) {
		Customer customer = getCustomer(purchaseDTO.getCustomerId());
		List<Book> books = validateBooksExist(purchaseDTO.getBookIds());

		// Calculate total price
		int		bookCount			= books.size();
		double	totalPrice			= books.stream().mapToDouble(book -> calculateBookPrice(book, bookCount)).sum();

		// Apply loyalty points if applicable
		boolean	loyaltyPointsUsed	= false;
		if (customer.getLoyaltyPoints() >= 10) {
			totalPrice -= books.stream()
					.filter(book -> book.getType() != BookType.NEW_RELEASE)
					.mapToDouble(book -> calculateBookPrice(book, bookCount))
					.min()
					.orElse(0.0);
			customer.setLoyaltyPoints(0);
			loyaltyPointsUsed = true;
		} else {
			customer.setLoyaltyPoints(customer.getLoyaltyPoints() + bookCount);
		}
		customerRepository.save(customer);

		Purchase purchase = new Purchase(customer, books, totalPrice, loyaltyPointsUsed);

		return purchaseMapper.convertToPurchaseDTO(purchaseRepository.save(purchase));
	}

	@Transactional
	public PurchaseDTO updatePurchase(Long id, PurchaseDTO purchaseDTO) {
		Purchase purchase = getPurchase(id);
		Customer customer = getCustomer(purchaseDTO.getCustomerId());
		List<Book> books = validateBooksExist(purchaseDTO.getBookIds());

		// Calculate new total price
		int		bookCount			= books.size();
		double	totalPrice			= books.stream().mapToDouble(book -> calculateBookPrice(book, bookCount)).sum();

		// Handle loyalty points
		boolean	loyaltyPointsUsed	= purchase.isLoyaltyPointsUsed();
		if (loyaltyPointsUsed) {
			totalPrice -= books.stream()
					.filter(book -> book.getType() != BookType.NEW_RELEASE)
					.mapToDouble(book -> calculateBookPrice(book, bookCount))
					.min()
					.orElse(0.0);
		} else {
			customer.setLoyaltyPoints(customer.getLoyaltyPoints() + bookCount);
			customerRepository.save(customer);
		}

		purchase.setTotalPrice(totalPrice);
		purchase.setBooks(books);

		return purchaseMapper.convertToPurchaseDTO(purchaseRepository.save(purchase));
	}

	public boolean deletePurchase(Long id) {
		Purchase purchase = getPurchase(id);
		purchaseRepository.delete(purchase);
		return true;
	}


	private List<Book> validateBooksExist(List<Long> bookIds) {
		List<Book> books = bookRepository.findAllById(bookIds);
		if (books.size() != bookIds.size()) {
			throw new BookNotFoundException("Some books not found. Please check book IDs.");
		}
		return books;
	}

	private Customer getCustomer(Long customerId) {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
	}

	private Purchase getPurchase(Long id) {
		return purchaseRepository.findById(id)
				.orElseThrow(() -> new PurchaseNotFoundException("Purchase not found with ID: " + id));
	}
}