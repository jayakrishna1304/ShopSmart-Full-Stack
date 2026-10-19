package com.example.CartService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private CartRepository db;

	@Mock
	private CartquantityInterface quantityClient;

	@InjectMocks
	private CartService cartService;

	private RequestDto requestDto;
	private ProductDto productDto;
	private  ResponseDto responseDto;
	private CartEntity existingCartEntity;

	@BeforeEach
	void setUp() {
		// Initialize test data before each test execution
		requestDto = new RequestDto();
		requestDto.setCustomerId(101);
		requestDto.setProductId(5);
		requestDto.setQuantity(2);

		productDto = new ProductDto();
		productDto.setProductId(5);
		productDto.setProductName("Test Shampoo");
		productDto.setStockLeft(10);

		existingCartEntity = new CartEntity();
		existingCartEntity.setId(1);
		existingCartEntity.setCustomerId(101);
		existingCartEntity.setProductId(5);
		existingCartEntity.setQuantity(3);
	}

	//           ADD TO CART TEST CASES          //

	@Test
	@DisplayName("Should successfully add a new item to cart when product exists and stock is sufficient")
	void addingToCart_NewItem_Success() {
		// Arrange
		when(quantityClient.getproduct(requestDto.getProductId())).thenReturn(productDto);
		when(db.findByCustomerIdAndProductId(requestDto.getCustomerId(), requestDto.getProductId()))
				.thenReturn(Optional.empty()); // No existing items in cart

		// Act
		ResponseEntity<String> response = cartService.addingtocart(requestDto);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Product successfully processed in cart!", response.getBody());
		verify(db, times(1)).save(any(CartEntity.class));
	}

	@Test
	@DisplayName("Should successfully increment quantity when item already exists in cart and stock is sufficient")
	void addingToCart_ExistingItem_Success() {
		// Arrange
		when(quantityClient.getproduct(requestDto.getProductId())).thenReturn(productDto);
		when(db.findByCustomerIdAndProductId(requestDto.getCustomerId(), requestDto.getProductId()))
				.thenReturn(Optional.of(existingCartEntity));

		// Act
		ResponseEntity<String> response = cartService.addingtocart(requestDto);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(5, existingCartEntity.getQuantity());
		verify(db, times(1)).save(existingCartEntity);
	}

	@Test
	@DisplayName("Should return 400 Bad Request when requested quantity exceeds available stock")
	void addingToCart_InsufficientStock_Failure() {
		// Arrange
		productDto.setStockLeft(4); // Total stock is 4
		existingCartEntity.setQuantity(3); // Already has 3 in cart
		// Total requested would be 3 + 2 = 5 (exceeds stock of 4)

		when(quantityClient.getproduct(requestDto.getProductId())).thenReturn(productDto);
		when(db.findByCustomerIdAndProductId(requestDto.getCustomerId(), requestDto.getProductId()))
				.thenReturn(Optional.of(existingCartEntity));

		// Act
		ResponseEntity<String> response = cartService.addingtocart(requestDto);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("Error: Only 4 units available in stock"));
		verify(db, never()).save(any(CartEntity.class)); // DB save must never be called
	}

	@Test
	@DisplayName("Should return 400 Bad Request when product ID does not exist in Product microservice")
	void addingToCart_ProductNotFound_Failure() {
		when(quantityClient.getproduct(requestDto.getProductId())).thenReturn(null);

		ResponseEntity<String> response = cartService.addingtocart(requestDto);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Error: The requested product ID does not exist.", response.getBody());
		verify(db, never()).save(any(CartEntity.class));
	}

	//DELETE PRODUCTS TEST CASES

	@Test
	@DisplayName("Should decrement quantity when removing items and remaining quantity is greater than zero")
	void deleteProducts_DecrementQuantity_Success() {
		responseDto.setQuantity(1);
		existingCartEntity.setQuantity(3);

		when(db.findByCustomerIdAndProductId(requestDto.getCustomerId(), requestDto.getProductId()))
				.thenReturn(Optional.of(existingCartEntity));

		cartService.deleteproducts(responseDto);

		assertEquals(2, existingCartEntity.getQuantity()); // 3 - 1 = 2
		verify(db, times(1)).save(existingCartEntity);
		verify(db, never()).delete(any(CartEntity.class));
	}

	@Test
	@DisplayName("Should completely delete row from DB when remaining quantity drops to zero or below")
	void deleteProducts_RemoveCompletely_Success() {
		requestDto.setQuantity(3);
		existingCartEntity.setQuantity(3);

		when(db.findByCustomerIdAndProductId(requestDto.getCustomerId(), requestDto.getProductId()))
				.thenReturn(Optional.of(existingCartEntity));

		cartService.deleteproducts(responseDto);

		verify(db, times(1)).delete(existingCartEntity);
		verify(db, never()).save(any(CartEntity.class));
	}
}
