package com.huigrowth.babycare.dto;

import com.huigrowth.babycare.entity.Baby;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宝宝响应DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class BabyResponse {
    private Long id;
    private String name;
    private Baby.Gender gender;
    private LocalDate birthday;
    private String avatar;
    private int ageInDays;
    private String ageDescription;
    private Long familyId;
    private LocalDateTime createdAt;
}