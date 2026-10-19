package com.shopsmart.retailer.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
public class Orderdto {
    private Integer orderId;
    private Integer customerId;
    private Integer shopId;
    private Double totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private Integer loyaltyPointsEarned = 0;
    private Integer loyaltyPointsRedeemed = 0;
    private String status = "PLACED";
    private LocalDateTime createdAt = LocalDateTime.now();



}
