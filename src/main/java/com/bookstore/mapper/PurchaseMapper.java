package com.bookstore.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.bookstore.dto.PurchaseDTO;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.Purchase;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

	PurchaseMapper INSTANCE = Mappers.getMapper(PurchaseMapper.class);

	@Mapping(source = "customer.id", target = "customerId")
	@Mapping(source = "books", target = "bookIds", qualifiedByName = "mapBooksToIds")
	PurchaseDTO convertToPurchaseDTO(Purchase purchase);

	@Mapping(source = "customerId", target = "customer", qualifiedByName = "mapCustomerIdToCustomer")
	@Mapping(source = "bookIds", target = "books", qualifiedByName = "mapBookIdsToBooks")
	Purchase convertToPurchase(PurchaseDTO purchaseDTO);

	List<PurchaseDTO> convertToPurchaseDTOList(List<Purchase> purchases);

	// Custom mapping methods
	@Named("mapBooksToIds")
	default List<Long> mapBooksToIds(List<Book> books) {
		return books.stream().map(Book::getId).collect(Collectors.toList());
	}

	@Named("mapBookIdsToBooks")
	default List<Book> mapBookIdsToBooks(List<Long> bookIds) {
		return bookIds.stream().map(id -> {
			Book book = new Book();
			book.setId(id);
			return book;
		}).collect(Collectors.toList());
	}

	@Named("mapCustomerIdToCustomer")
	default Customer mapCustomerIdToCustomer(Long customerId) {
		if (customerId == null) {
			return null;
		}
		Customer customer = new Customer();
		customer.setId(customerId);
		return customer;
	}
}