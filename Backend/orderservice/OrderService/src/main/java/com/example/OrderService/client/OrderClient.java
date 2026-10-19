package com.example.OrderService.client;

import com.example.OrderService.config.FeignConfig;
import com.example.OrderService.dto.ShopDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "shop-service",
        url = "http://localhost:8085",
        configuration = FeignConfig.class
)
public interface OrderClient {

    @GetMapping("/shopsmart/shop/retailer/{retailerId}")
    List<ShopDto> getShopsByRetailer(
            @PathVariable("retailerId") Integer retailerId
    );
}