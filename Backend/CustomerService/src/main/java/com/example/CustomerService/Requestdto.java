package com.example.CustomerService;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Requestdto {
    private int customerId;
    private String customerName;
    private String email;
    private Date createdDate;
}
