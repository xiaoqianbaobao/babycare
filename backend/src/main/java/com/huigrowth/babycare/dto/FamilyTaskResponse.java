package com.huigrowth.babycare.dto;

import com.huigrowth.babycare.entity.FamilyTask;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 家庭任务响应DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class FamilyTaskResponse {
    private Long id;
    private Long familyId;
    private String familyName;
    private String title;
    private String description;
    private Long assigneeId;
    private String assigneeUsername;
    private String assigneeNickname;
    private Long assignedById;
    private String assignedByUsername;
    private String assignedByNickname;
    private LocalDateTime dueDate;
    private FamilyTask.TaskPriority priority;
    private FamilyTask.TaskStatus status;
    private FamilyTask.TaskCategory category;
    private LocalDateTime completedAt;
    private Long completedById;
    private String completedByUsername;
    private String completedByNickname;
    private String completionNotes;
    private LocalDateTime reminderTime;
    private Boolean isRecurring;
    private String recurrencePattern;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}