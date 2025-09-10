package com.huigrowth.babycare.dto;

import com.huigrowth.babycare.entity.EducationPlan;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教育计划响应DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class EducationPlanResponse {
    private Long id;
    private Long babyId;
    private String babyName;
    private String name;
    private String description;
    private EducationPlan.EducationCategory category;
    private EducationPlan.PlanStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer targetAgeMonths;
    private Integer difficultyLevel;
    private String goals;
    private Integer progressPercentage;
    private String createdBy;
    private String createdByNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int totalActivities;
    private int completedActivities;
    private int pendingActivities;
}