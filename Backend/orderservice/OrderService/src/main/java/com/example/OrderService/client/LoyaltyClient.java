package com.example.OrderService.client;

import com.example.OrderService.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "loyalty-service",
        url = "http://localhost:8090",
        configuration = FeignConfig.class

)
public interface LoyaltyClient {



    @GetMapping("/shopsmart/loyalty/customer/{customerId}/points-avail")
    Integer availablepoints(
            @PathVariable("customerId") Integer customerId
    );




    @PatchMapping(
            "/shopsmart/loyalty/customer/{customerId}/add-points"
    )
    void addPoints(
            @PathVariable("customerId") Integer customerId,
            @RequestParam("points") Integer points
    );


    // =========================================================
    // REDEEM POINTS
    // =========================================================

    @PatchMapping(
            "/shopsmart/loyalty/customer/{customerId}/redeem-points"
    )
    void pointstoredeem(
            @PathVariable("customerId") Integer customerId,
            @RequestParam("points") Integer points
    );
}