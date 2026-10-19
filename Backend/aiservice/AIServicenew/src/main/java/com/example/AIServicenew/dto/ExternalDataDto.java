package com.example.AIServicenew.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExternalDataDto {

    // =========================
    // CUSTOMER
    // =========================

    private Long customerId;

    private String customerName;

    private String customerEmail;


    // =========================
    // CART
    // =========================

    private Long cartItemId;

    private Integer productId;

    private String productName;

    private Integer cartQuantity;

    private LocalDateTime cartUpdatedAt;


    // =========================
    // ORDER
    // =========================

    private Long orderId;

    private Integer shopId;

    @JsonAlias("totalAmount")
    private Double orderTotalAmount;

    @JsonAlias("createdAt")
    private LocalDateTime orderCreatedAt;

    private String paymentMethod;

    private String paymentStatus;

    private String status;

    private Integer loyaltyPointsEarned;

    private Integer loyaltyPointsRedeemed;
}