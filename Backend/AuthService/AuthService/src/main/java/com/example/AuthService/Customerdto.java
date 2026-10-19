package com.example.AuthService;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Customerdto {
    private int customerId;
    private String customerName;
    private String email;
    private Date createdDate;
}
