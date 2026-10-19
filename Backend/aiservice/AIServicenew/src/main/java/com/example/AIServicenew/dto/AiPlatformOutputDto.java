package com.example.AIServicenew.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiPlatformOutputDto {
    private Long referenceId;
    private String insightType;     // TRENDING_BUNDLE, LOYALTY_TIER, ABANDONED_CART, DORMANCY_NUDGE
    private String itemSubjectName;
    private String aiAnalysisResult;
    private String actionableNudge;
}
