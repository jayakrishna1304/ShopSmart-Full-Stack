package com.example.CartService;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseDto {
    private Integer id;
    private Integer customerId;
    private String productName;
    private String description;
    private Double originalPrice;
    private Double discount = 0.00;
    private String category;
    private Integer shopId;
    private String imageUrl;

    private Integer productId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
