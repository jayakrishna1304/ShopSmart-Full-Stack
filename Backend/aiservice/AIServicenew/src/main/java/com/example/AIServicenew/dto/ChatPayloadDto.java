package com.example.AIServicenew.dto;

import lombok.Data;

@Data
public class ChatPayloadDto {
    private String userPrompt;
    private String userRole;         // "customer" or "retailer"
    private Integer activeContextId; // customerId or shopId
}
