package com.example.CartService;

import com.example.CartService.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        url = "http://localhost:8082",
        configuration = FeignConfig.class
)
public interface CartquantityInterface {

    @GetMapping("/shopsmart/product/{id}")
    ProductDto getproduct(
            @PathVariable("id") Integer id
    );
}