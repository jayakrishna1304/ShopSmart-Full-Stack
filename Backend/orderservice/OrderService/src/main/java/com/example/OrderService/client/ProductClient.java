package com.example.OrderService.client;

import com.example.OrderService.config.FeignConfig;
import com.example.OrderService.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "product-service",
        url = "http://localhost:8082",
        configuration = FeignConfig.class
)
public interface ProductClient {

    @GetMapping("/shopsmart/product/{id}")
    ProductDto getProductDetails(
            @PathVariable("id") Integer id
    );

    @PutMapping("/shopsmart/product/{id}/use-stock")
    void useStock(
            @PathVariable("id") Integer id,
            @RequestParam("quantity") Integer quantity
    );
}