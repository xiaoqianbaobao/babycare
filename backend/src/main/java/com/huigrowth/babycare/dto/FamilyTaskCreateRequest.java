package com.huigrowth.babycare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建家庭任务请求DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class FamilyTaskCreateRequest {

    @NotBlank(message = "任务标题不能为空")
    @Size(min = 1, max = 100, message = "标题长度必须在1-100个字符之间")
    private String title;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @NotNull(message = "家庭ID不能为空")
    private Long familyId;

    @NotNull(message = "分配者不能为空")
    private Long assigneeId; // 分配给的用户ID

    private LocalDateTime dueDate;

    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT

    @NotBlank(message = "任务类别不能为空")
    private String category; // FEEDING, DIAPER, BATH, PLAY, EDUCATION, MEDICAL, SLEEP, OTHER

    private LocalDateTime reminderTime; // 提醒时间
    
    // Explicitly adding getter methods to resolve potential compilation issues
    public Long getFamilyId() {
        return familyId;
    }
    
    public Long getAssigneeId() {
        return assigneeId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public String getCategory() {
        return category;
    }
    
    public LocalDateTime getReminderTime() {
        return reminderTime;
    }
}