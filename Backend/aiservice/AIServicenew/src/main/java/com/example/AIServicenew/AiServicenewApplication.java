package com.example.AIServicenew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AiServicenewApplication {
	public static void main(String[] args) {
		SpringApplication.run(AiServicenewApplication.class, args);
	}
}
