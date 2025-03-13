package com.bookstore.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.dto.CustomerDTO;
import com.bookstore.exception.CustomerNotFoundException;
import com.bookstore.mapper.CustomerMapper;
import com.bookstore.model.Customer;
import com.bookstore.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;

	@Transactional(readOnly = true)
	public List<CustomerDTO> getAllCustomers() {
		return customerMapper.convertToCustomerList(customerRepository.findAll());
	}

	@Transactional(readOnly = true)
	public CustomerDTO getCustomerById(Long id) {
		Customer customer = getCustomer(id);
		return customerMapper.convertToCustomerDTO(customer);
	}

	@Transactional
	public CustomerDTO createCustomer(CustomerDTO customerDTO) {
		Customer customer = customerMapper.convertToCustomer(customerDTO);
		return customerMapper.convertToCustomerDTO(customerRepository.save(customer));
	}

	@Transactional
	public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
		Customer customer = getCustomer(id);

		customer.setName(customerDTO.getName());
		customer.setLoyaltyPoints(customerDTO.getLoyaltyPoints());

		return customerMapper.convertToCustomerDTO(customerRepository.save(customer));
	}

	@Transactional
	public boolean deleteCustomer(Long id) {
		Customer customer = getCustomer(id);
		customerRepository.delete(customer);
		return true;
	}

	private Customer getCustomer(Long id) {
		return customerRepository.findById(id)
				.orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " not found"));
	}
}
