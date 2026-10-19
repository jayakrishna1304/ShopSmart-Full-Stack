package com.shopsmart.retailer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RetailerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetailerApplication.class, args);
    }

}
