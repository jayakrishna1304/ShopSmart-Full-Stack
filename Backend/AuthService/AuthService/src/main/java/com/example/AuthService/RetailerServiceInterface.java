package com.example.AuthService;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "retailer-service", url = "http://localhost:8084")
public interface RetailerServiceInterface {

    @PostMapping("/shopsmart/retailer")
    String createretailer(@RequestBody Retailerdto retailer);
}
