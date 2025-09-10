package com.huigrowth.babycare.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 家庭响应DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class FamilyResponse {
    private Long id;
    private String name;
    private String description;
    private String inviteCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FamilyMemberResponse> members;
    private List<BabyResponse> babies;
    private int memberCount;
    private int babyCount;
}