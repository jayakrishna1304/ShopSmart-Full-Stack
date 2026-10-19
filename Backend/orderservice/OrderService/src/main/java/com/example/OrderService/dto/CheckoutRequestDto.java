package com.example.OrderService.dto;

import lombok.Data;

@Data
public class CheckoutRequestDto {

    private Integer customerId;

    private Integer shopId;

    private String paymentMethod; // 'UPI' or 'COD'

    private Integer pointsToRedeem;


    private String upiTransactionId;

    private String deliveryAddress;
}