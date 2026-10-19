package com.example.OrderService.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LoyaltyTransactionResponseDto {

    private Integer transactionId;

    private Integer customerId;

    private Integer orderId;

    private String transactionType;

    private Integer points;

    private String description;

    private LocalDateTime createdAt;
}