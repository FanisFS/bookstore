package com.bookstore.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.bookstore.dto.PurchaseDTO;
import com.bookstore.model.Book;
import com.bookstore.model.BookType;
import com.bookstore.model.Customer;
import com.bookstore.model.Purchase;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CustomerRepository;
import com.bookstore.repository.PurchaseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
class PurchaseControllerIntegrationTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PurchaseRepository purchaseRepository;

	private MockMvc mockMvc;

	private Book book1, book2, book3;
	private Customer customer;
	private Purchase purchase;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		purchaseRepository.deleteAll();
		bookRepository.deleteAll();
		customerRepository.deleteAll();

		book1 = bookRepository.save(new Book(null, "Dungeons & Dragons 5th edition", "Rodney Thompson", 39.99, BookType.REGULAR));
		book2 = bookRepository.save(new Book(null, "The Hobbit", "J.R.R. Tolkien", 25.99, BookType.REGULAR));
		book3 = bookRepository.save(new Book(null, "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", 29.99, BookType.NEW_RELEASE));

		customer = customerRepository.save(new Customer(null, "John Doe", 0));

		purchase = purchaseRepository.save(new Purchase(customer, Arrays.asList(book1, book2), 65.98, false));
	}

	// ===========================
	// getAllPurchases()
	// ===========================

	@Test
	public void testGetAllPurchases_ReturnsAllPurchases() throws Exception {
		mockMvc.perform(get("/api/purchases"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	public void testGetAllPurchases_EmptyDatabase() throws Exception {
		purchaseRepository.deleteAll();

		mockMvc.perform(get("/api/purchases"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").isEmpty());
	}

	// ===========================
	// getPurchaseById(Long)
	// ===========================

	@Test
	public void testGetPurchaseById_ReturnsPurchase() throws Exception {
		mockMvc.perform(get("/api/purchases/" + purchase.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(purchase.getId()))
		.andExpect(jsonPath("$.totalPrice").value(purchase.getTotalPrice()));
	}

	@Test
	public void testGetPurchaseById_NotFound() throws Exception {
		mockMvc.perform(get("/api/purchases/100"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.message").value("Purchase not found with ID: 100"));
	}

	@Test
	public void testGetPurchaseById_NegativeId() throws Exception {
		mockMvc.perform(get("/api/purchases/-1"))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void testGetPurchaseById_InvalidIdFormat() throws Exception {
		mockMvc.perform(get("/api/purchases/abc"))
		.andExpect(status().isBadRequest());
	}

	// ===========================
	// createPurchase(PurchaseDTO)
	// ===========================

	@Test
	public void testCreatePurchase_CreatesSuccessfully() throws Exception {
		PurchaseDTO newPurchase = new PurchaseDTO(null, customer.getId(), Arrays.asList(book1.getId(), book2.getId()), 65.98);

		mockMvc.perform(post("/api/purchases")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newPurchase)))
		.andExpect(status().isCreated());
	}

	@Test
	public void testCreatePurchase_InvalidCustomer() throws Exception {
		PurchaseDTO newPurchase = new PurchaseDTO(null, 100L, Arrays.asList(book1.getId()), 39.99);

		mockMvc.perform(post("/api/purchases")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newPurchase)))
		.andExpect(status().isNotFound());
	}

	@Test
	public void testCreatePurchase_InvalidBook() throws Exception {
		PurchaseDTO newPurchase = new PurchaseDTO(null, customer.getId(), Collections.singletonList(100L), 39.99);

		mockMvc.perform(post("/api/purchases")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newPurchase)))
		.andExpect(status().isNotFound());
	}

	// ===========================
	// updatePurchase(Long, PurchaseDTO)
	// ===========================

	@Test
	public void testUpdatePurchase_UpdatesSuccessfully() throws Exception {
		PurchaseDTO updatedPurchase = new PurchaseDTO(purchase.getId(), customer.getId(), Arrays.asList(book2.getId(), book3.getId()), 55.98);

		mockMvc.perform(put("/api/purchases/" + purchase.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedPurchase)))
		.andExpect(status().isOk());
	}

	@Test
	public void testUpdatePurchase_NotFound() throws Exception {
		PurchaseDTO updatedPurchase = new PurchaseDTO(100L, customer.getId(), Arrays.asList(book1.getId()), 39.99);

		mockMvc.perform(put("/api/purchases/100")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedPurchase)))
		.andExpect(status().isNotFound());
	}

	@Test
	public void testUpdatePurchase_InvalidCustomer() throws Exception {
		PurchaseDTO updatedPurchase = new PurchaseDTO(purchase.getId(), 100L, Arrays.asList(book1.getId()), 39.99);

		mockMvc.perform(put("/api/purchases/" + purchase.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedPurchase)))
		.andExpect(status().isNotFound());
	}
	
	@Test
	public void testUpdatePurchase_NegativeIy() throws Exception {
		PurchaseDTO updatedPurchase = new PurchaseDTO(purchase.getId(), customer.getId(), Arrays.asList(book2.getId(), book3.getId()), 55.98);

		mockMvc.perform(put("/api/purchases/-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedPurchase)))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdatePurchase_NonNumericId() throws Exception {
		PurchaseDTO updatedPurchase = new PurchaseDTO(purchase.getId(), customer.getId(), Arrays.asList(book2.getId(), book3.getId()), 55.98);

		mockMvc.perform(put("/api/purchases/abc")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedPurchase)))
		.andExpect(status().isBadRequest());
	}

	// ===========================
	// deletePurchase(Long)
	// ===========================

	@Test
	public void testDeletePurchase_DeletesSuccessfully() throws Exception {
		mockMvc.perform(delete("/api/purchases/" + purchase.getId()))
		.andExpect(status().isNoContent());
	}

	@Test
	public void testDeletePurchase_NotFound() throws Exception {
		mockMvc.perform(delete("/api/purchases/100"))
		.andExpect(status().isNotFound());
	}

	@Test
	public void testDeletePurchase_NegativeId() throws Exception {
		mockMvc.perform(delete("/api/purchases/-1"))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void testDeletePurchase_InvalidIdFormat() throws Exception {
		mockMvc.perform(delete("/api/purchases/abc"))
		.andExpect(status().isBadRequest());
	}
}
