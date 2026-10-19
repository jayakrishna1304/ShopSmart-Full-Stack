package com.shopsmart.retailer;

import com.shopsmart.retailer.dto.Orderdto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service", url = "http://localhost:8087/shopsmart/orders")
public interface Fetchorders {

    @GetMapping("/shop/{shopId}")
    List<Orderdto> orderclient(@PathVariable("shopId") Integer shopId);
}
