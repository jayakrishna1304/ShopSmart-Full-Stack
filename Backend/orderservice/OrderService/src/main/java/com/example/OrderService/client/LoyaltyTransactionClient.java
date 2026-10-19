package com.example.OrderService.client;

import com.example.OrderService.dto.LoyaltyTransactionRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "loyalty-transaction-service",
        url = "http://localhost:8091"
)
public interface LoyaltyTransactionClient {

    @PostMapping("/shopsmart/loyaltyTransaction")
    void createTransaction(
            @RequestBody LoyaltyTransactionRequestDto request
    );
}