package com.example.OrderService.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VoucherDto {

    private Integer voucherId;

    private String voucherCode;

    private Double discountPercentage;

    private Boolean active;

    private Double minimumOrderAmount;

    private LocalDate startDate;

    private LocalDate endDate;
}