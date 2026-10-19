package com.example.CustomerService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	@Mock
	private CustomerRepository customerdb;

	@InjectMocks
	private CustomerService service;


	// CREATE CUSTOMER

	@Test
	void saveCustomerSuccessfully() {

		Requestdto request = new Requestdto();

		request.setCustomer_id(1);
		request.setCustomer_name("Krishna");
		request.setEmail("krishna@gmail.com");

		when(customerdb.save(any(CustomerEntity.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		String result = service.savetocustomerdb(request);

		assertEquals("customer registered", result);

		verify(customerdb).save(any(CustomerEntity.class));
	}


	// GET CUSTOMER - SUCCESS

	@Test
	void searchCustomerByIdSuccessfully() {

		CustomerEntity customer = new CustomerEntity();

		customer.setCustomer_id(1);
		customer.setCustomer_name("Krishna");
		customer.setCustomer_email("krishna@gmail.com");

		when(customerdb.findById("1"))
				.thenReturn(Optional.of(customer));

		Optional<CustomerEntity> result =
				service.searchbyId("1");

		assertTrue(result.isPresent());

		assertEquals(
				"Krishna",
				result.get().getCustomer_name()
		);

		verify(customerdb).findById("1");
	}


	// GET CUSTOMER - NOT FOUND

	@Test
	void searchCustomerByIdNotFound() {

		when(customerdb.findById("999"))
				.thenReturn(Optional.empty());

		Optional<CustomerEntity> result =
				service.searchbyId("999");

		assertTrue(result.isEmpty());

		verify(customerdb).findById("999");
	}


	// DELETE CUSTOMER

	@Test
	void deleteCustomerSuccessfully() {

		when(customerdb.existsById("1"))
				.thenReturn(true);

		String result =
				service.deleteCustomer("1");

		assertEquals(
				"Customer deleted successfully",
				result
		);

		verify(customerdb).existsById("1");

		verify(customerdb).deleteById("1");
	}
}