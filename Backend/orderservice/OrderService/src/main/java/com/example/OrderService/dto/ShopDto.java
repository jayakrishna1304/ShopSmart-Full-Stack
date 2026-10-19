package com.example.OrderService.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ShopDto {

    private Integer shopId;
    private String shopName;
    private String address;
    private Integer ownerId;
    private String status;
}