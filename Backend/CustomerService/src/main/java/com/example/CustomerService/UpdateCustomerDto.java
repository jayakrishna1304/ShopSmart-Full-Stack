package com.example.CustomerService;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerDto {

    private String customer_name;
    private String phone;
    private String address;
    private String city;
    private String pincode;
}