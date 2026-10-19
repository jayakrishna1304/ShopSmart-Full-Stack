package com.example.AuthService;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer-service", url = "http://localhost:8086")
@Service
public interface AuthserviceInterface {
    @PostMapping("/customers")
    public void createcustomer(@RequestBody Customerdto customerobj);
}
