package com.example.AIServicenew.client;

import com.example.AIServicenew.dto.ExternalDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "cart-service", url = "http://localhost:8083")
public interface CartDataClient {
    @GetMapping("/shopsmart/cart/items/all-dump")
    List<ExternalDataDto> getAllActiveCartSessionsDump();
}
