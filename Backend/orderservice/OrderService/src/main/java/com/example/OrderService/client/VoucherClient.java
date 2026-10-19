package com.example.OrderService.client;

import com.example.OrderService.dto.VoucherDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "voucher-service",
        url = "http://localhost:8092"
)
public interface VoucherClient {

    @GetMapping("/voucher/code/{voucherCode}")
    VoucherDto getVoucherByCode(
            @PathVariable("voucherCode") String voucherCode
    );
}