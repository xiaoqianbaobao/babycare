package com.huigrowth.babycare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 家庭任务实体
 * 
 * @author HuiGrowth Team
 */
@Entity
@Table(name = "family_tasks", indexes = {
    @Index(name = "idx_family_task_family", columnList = "family_id"),
    @Index(name = "idx_family_task_assigned_by", columnList = "assigned_by"),
    @Index(name = "idx_family_task_status", columnList = "status"),
    @Index(name = "idx_family_task_due_date", columnList = "due_date")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"family", "assignedBy", "completedBy"})
@ToString(exclude = {"family", "assignedBy", "completedBy"})
public class FamilyTask extends BaseEntity {

    @NotNull(message = "家庭不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @NotBlank(message = "任务标题不能为空")
    @Size(min = 1, max = 100, message = "标题长度必须在1-100个字符之间")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "assigned_to", columnDefinition = "JSON")
    private String assignedTo; // JSON格式存储分配的用户ID列表

    @NotNull(message = "分配者不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TaskCategory category;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by")
    private User completedBy;

    @Column(name = "completion_notes", length = 500)
    private String completionNotes; // 完成备注

    @Column(name = "reminder_time")
    private LocalDateTime reminderTime; // 提醒时间

    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = false; // 是否重复任务

    @Column(name = "recurrence_pattern", length = 100)
    private String recurrencePattern; // 重复模式

    /**
     * 任务优先级枚举
     */
    public enum TaskPriority {
        LOW("低"),
        MEDIUM("中"),
        HIGH("高"),
        URGENT("紧急");

        private final String description;

        TaskPriority(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        PENDING("待处理"),
        IN_PROGRESS("进行中"),
        COMPLETED("已完成"),
        CANCELLED("已取消"),
        OVERDUE("已过期");

        private final String description;

        TaskStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 任务类别枚举
     */
    public enum TaskCategory {
        FEEDING("喂养"),
        DIAPER("换尿片"),
        BATH("洗澡"),
        PLAY("游戏"),
        EDUCATION("教育"),
        MEDICAL("医疗"),
        SLEEP("睡眠"),
        OTHER("其他");

        private final String description;

        TaskCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}