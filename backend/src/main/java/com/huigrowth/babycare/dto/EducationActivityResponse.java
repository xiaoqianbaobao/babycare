package com.huigrowth.babycare.dto;

import com.huigrowth.babycare.entity.EducationActivity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教育活动响应DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class EducationActivityResponse {
    private Long id;
    private Long educationPlanId;
    private String educationPlanName;
    private String name;
    private String description;
    private EducationActivity.ActivityType type;
    private EducationActivity.ActivityStatus status;
    private LocalDateTime scheduledTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private Integer durationMinutes;
    private String materials;
    private String instructions;
    private String notes;
    private Integer rating;
    private Integer completionPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}