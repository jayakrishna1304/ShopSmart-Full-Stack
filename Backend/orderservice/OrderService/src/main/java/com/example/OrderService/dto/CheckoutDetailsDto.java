package com.example.OrderService.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CheckoutDetailsDto {

    private Integer customerId;

    private List<CartResponseDto> items;

    private double subTotal;

    private Integer availablePoints;

    private String loyaltyTier;

    private List<VoucherDto> vouchers;
}