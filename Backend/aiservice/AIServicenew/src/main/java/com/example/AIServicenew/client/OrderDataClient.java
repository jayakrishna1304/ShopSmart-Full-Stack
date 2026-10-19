package com.example.AIServicenew.client;

import com.example.AIServicenew.dto.ExternalDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "order-service", url = "http://localhost:8087")
public interface OrderDataClient {
    @GetMapping("/shopsmart/orders/history/all-dump")
    List<ExternalDataDto> getAllHistoricalPurchasesDump();
}
