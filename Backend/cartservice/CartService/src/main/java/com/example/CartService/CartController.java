package com.example.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopsmart/cart")
@Tag(name = "Cart Service API", description = "Endpoints for managing customer shopping carts and stock validation")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(
            summary = "Add or increment product in cart",
            description = "Adds a product to the user's cart. If the product already exists, it increments the quantity. Automatically cross-verifies remaining stock with the Product microservice."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully processed and saved in cart"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Requested quantity exceeds available stock or invalid product ID"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Product microservice is currently unreachable")
    })
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody RequestDto requestDto) {

        return cartService.addingtocart(requestDto);
    }

    // 🌟 ADDED ENDPOINT: Fetches EVERY single active item row for the AI engine!
    // This provides the data your standalone AI service needs to check for the 1-week abandoned cart rule.
    @GetMapping("/items/all-dump")
    @Operation(summary = "Internal AI Link: Fetch all raw active database cart items for algorithmic mining templates")
    public ResponseEntity<List<CartEntity>> getAllActiveCartSessionsDump() {
        // Leverages your existing findallproducts service method!
        return cartService.findallproducts();
    }

    // NEW ENDPOINT: Fetches clean cart DTO lines specifically for the Order Service checkout engine!
    @GetMapping("/{customerId}")
    @Operation(summary = "Fetch basket rows mapped to a single customer ID context")
    public ResponseEntity<List<ResponseDto>> getCartByCustomer(@PathVariable("customerId") Integer customerId) {
        return cartService.getCartByCustomer(customerId);
    }

    // NEW ENDPOINT: Sweeps the cart clear upon a successful payment checkout transaction
    @DeleteMapping("/clear/{customerId}")
    @Operation(summary = "Wipe out shopping session items post-checkout")
    public ResponseEntity<String> clearCart(@PathVariable("customerId") Integer customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.ok("Customer cart wiped successfully.");
    }

    @Operation(
            summary = "Decrement or remove product from cart",
            description = "Reduces the item count of a product in the user's cart by the requested quantity. If the quantity drops to zero or below, the product line is completely deleted from the database."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart items successfully decremented or removed from the database")
    })
    @PostMapping("/remove")
    public ResponseEntity<String> removeItems(@RequestBody ResponseDto requestDto) {
        cartService.deleteproducts(requestDto);
        return ResponseEntity.ok("Cart items successfully decremented or removed.");
    }
}
