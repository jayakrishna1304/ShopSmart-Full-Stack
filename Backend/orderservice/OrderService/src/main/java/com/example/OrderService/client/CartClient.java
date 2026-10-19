package com.example.OrderService.client;

import com.example.OrderService.config.FeignConfig;
import com.example.OrderService.dto.CartResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "cart-service", url = "http://localhost:8083",configuration = FeignConfig.class
)
public interface CartClient {

    @GetMapping("/shopsmart/cart/{customerId}")
    List<CartResponseDto> getCartByCustomer(@PathVariable("customerId") Integer customerId);

    @DeleteMapping("/shopsmart/cart/clear/{customerId}")
    void clearCart(@PathVariable("customerId") Integer customerId);
}
