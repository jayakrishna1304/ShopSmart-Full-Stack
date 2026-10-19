package com.example.OrderService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoyaltyTransactionRequestDto {

    private Integer customerId;

    private Integer orderId;

    private String transactionType;

    private Integer points;

    private String description;
}