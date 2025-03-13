package com.bookstore.controller.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bookstore.controller.PurchaseController;
import com.bookstore.dto.PurchaseDTO;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.GlobalExceptionHandler;
import com.bookstore.exception.PurchaseNotFoundException;
import com.bookstore.service.PurchaseService;
import com.fasterxml.jackson.databind.ObjectMapper;

class PurchaseControllerUnitTest {

	@Mock
	private PurchaseService purchaseService;

	@InjectMocks
	private PurchaseController purchaseController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	private PurchaseDTO purchaseDTO1, purchaseDTO2;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(purchaseController)
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
		objectMapper = new ObjectMapper();

		purchaseDTO1 = new PurchaseDTO(1L, 1L, List.of(1L, 2L), 59.99);
		purchaseDTO2 = new PurchaseDTO(2L, 2L, List.of(3L), 25.99);
	}

	// ===========================
	// Test getAllPurchases()
	// ===========================

	@Test
	public void testGetAllPurchases_ReturnsAllPurchases() throws Exception {
		given(purchaseService.getAllPurchases()).willReturn(List.of(purchaseDTO1, purchaseDTO2));

		mockMvc.perform(get("/api/purchases"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	public void testGetAllPurchases_EmptyDatabase() throws Exception {
		given(purchaseService.getAllPurchases()).willReturn(List.of());

		mockMvc.perform(get("/api/purchases"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").isEmpty());
	}

	// ===========================
	// Test getPurchaseById(String)
	// ===========================

	@Test
	public void testGetPurchaseById_ReturnsPurchase() throws Exception {
		given(purchaseService.getPurchaseById(1L)).willReturn(purchaseDTO1);

		mockMvc.perform(get("/api/purchases/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.totalPrice").value(purchaseDTO1.getTotalPrice()));
	}

	@Test
	public void testGetPurchaseById_NotFound() throws Exception {
		given(purchaseService.getPurchaseById(100L)).willThrow(new PurchaseNotFoundException("Purchase not found with ID: 100"));

		mockMvc.perform(get("/api/purchases/100"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.message").value("Purchase not found with ID: 100"));
	}

	@Test
	public void testGetPurchaseById_InvalidIdFormat() throws Exception {
		mockMvc.perform(get("/api/purchases/abc"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Invalid purchase ID format: abc"));
	}

	@Test
	public void testGetPurchaseById_NegativeId() throws Exception {
		mockMvc.perform(get("/api/purchases/-1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Purchase ID must be greater than or equal to 0"));
	}

	// ===========================
	// Test createPurchase(PurchaseDTO)
	// ===========================

	@Test
	public void testCreatePurchase_CreatesPurchaseSuccessfully() throws Exception {
		given(purchaseService.createPurchase(any(PurchaseDTO.class))).willReturn(purchaseDTO1);

		mockMvc.perform(post("/api/purchases")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(purchaseDTO1)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.totalPrice").value(purchaseDTO1.getTotalPrice()));
	}

	@Test
	public void testCreatePurchase_InvalidCustomer() throws Exception {
		PurchaseDTO newPurchase = new PurchaseDTO(null, null, List.of(1L, 2L), 59.99);

		mockMvc.perform(post("/api/purchases")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newPurchase)))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void testCreatePurchase_InvalidBooks() throws Exception {
		given(purchaseService.createPurchase(any(PurchaseDTO.class))).willThrow(new BookNotFoundException("Some books not found. Please check book IDs."));
		PurchaseDTO newPurchase = new PurchaseDTO(1L, 1L, List.of(999L), 59.99);

		mockMvc.perform(post("/api/purchases")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newPurchase)))
		.andExpect(status().isNotFound());
	}

	// ===========================
	// Test updatePurchase(String, PurchaseDTO)
	// ===========================

	@Test
	public void testUpdatePurchase_SuccessfulUpdate() throws Exception {
		given(purchaseService.updatePurchase(1L, purchaseDTO1)).willReturn(purchaseDTO1);

		mockMvc.perform(put("/api/purchases/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(purchaseDTO1)))
		.andExpect(status().isOk());
	}

	@Test
	public void testUpdatePurchase_NotFound() throws Exception {
		given(purchaseService.updatePurchase(any(), any(PurchaseDTO.class))).willThrow(new PurchaseNotFoundException("Purchase not found with ID: 100"));

		mockMvc.perform(put("/api/purchases/100")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(purchaseDTO1)))
		.andExpect(status().isNotFound());
	}

	@Test
	public void testUpdatePurchase_InvalidIdFormat() throws Exception {
		mockMvc.perform(put("/api/purchases/abc")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(purchaseDTO1)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Invalid purchase ID format: abc"));
	}

	@Test
	public void testUpdatePurchase_InvalidBooks() throws Exception {
		given(purchaseService.updatePurchase(any(), any(PurchaseDTO.class))).willThrow(new BookNotFoundException("Some books not found. Please check book IDs."));

		mockMvc.perform(put("/api/purchases/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(purchaseDTO1)))
		.andExpect(status().isNotFound());
	}

	// ===========================
	// Test deletePurchase(String)
	// ===========================

	@Test
	public void testDeletePurchase_DeletesPurchaseSuccessfully() throws Exception {
		given(purchaseService.deletePurchase(2L)).willReturn(true);

		mockMvc.perform(delete("/api/purchases/2"))
		.andExpect(status().isNoContent());
	}

	@Test
	public void testDeletePurchase_NotFound() throws Exception {
		given(purchaseService.deletePurchase(100L)).willThrow(new PurchaseNotFoundException("Purchase not found with ID: 100"));

		mockMvc.perform(delete("/api/purchases/100"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.message").value("Purchase not found with ID: 100"));
	}

	@Test
	public void testDeletePurchase_InvalidIdFormat() throws Exception {
		mockMvc.perform(delete("/api/purchases/abc"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Invalid purchase ID format: abc"));
	}

	@Test
	public void testDeletePurchase_NegativeId() throws Exception {
		mockMvc.perform(delete("/api/purchases/-1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Purchase ID must be greater than or equal to 0"));
	}
}
