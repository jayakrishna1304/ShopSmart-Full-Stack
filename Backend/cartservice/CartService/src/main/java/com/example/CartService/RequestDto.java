package com.example.CartService;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDto {
    private Integer customerId;
    private Integer productId;
    private Integer quantity;
}
