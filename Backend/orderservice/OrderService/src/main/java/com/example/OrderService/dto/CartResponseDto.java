package com.example.OrderService.dto;

import lombok.Data;

@Data
public class CartResponseDto {
    private Integer id;
    private Integer customerId;
    private Integer productId;
    private Integer quantity;
}
