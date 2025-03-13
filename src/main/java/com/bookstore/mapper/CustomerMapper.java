package com.bookstore.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.bookstore.dto.CustomerDTO;
import com.bookstore.model.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
	CustomerDTO			convertToCustomerDTO	(Customer customer);
	List<CustomerDTO>	convertToCustomerList	(List<Customer> customer);

	Customer	convertToCustomer	(CustomerDTO customerDTO);
}