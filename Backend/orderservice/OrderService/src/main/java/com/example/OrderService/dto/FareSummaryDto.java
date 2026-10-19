package com.example.OrderService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FareSummaryDto {

    private Double subTotal;

    private Double voucherDiscount;

    private Integer pointsRedeemed;

    private Double finalPayableAmount;

    private Integer pointsToBeEarned;
}