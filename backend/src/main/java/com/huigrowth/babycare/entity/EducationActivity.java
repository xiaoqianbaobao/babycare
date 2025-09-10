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
 * 教育活动实体
 * 
 * @author HuiGrowth Team
 */
@Entity
@Table(name = "education_activities", indexes = {
    @Index(name = "idx_education_activity_plan", columnList = "education_plan_id"),
    @Index(name = "idx_education_activity_status", columnList = "status"),
    @Index(name = "idx_education_activity_scheduled", columnList = "scheduled_time")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"educationPlan"})
@ToString(exclude = {"educationPlan"})
public class EducationActivity extends BaseEntity {

    @NotNull(message = "教育计划不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_plan_id", nullable = false)
    private EducationPlan educationPlan;

    @NotBlank(message = "活动名称不能为空")
    @Size(min = 1, max = 100, message = "活动名称长度必须在1-100个字符之间")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "活动类型不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ActivityType type;

    @NotNull(message = "活动状态不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ActivityStatus status = ActivityStatus.PENDING;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime; // 计划时间

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime; // 实际开始时间

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime; // 实际结束时间

    @Column(name = "duration_minutes")
    private Integer durationMinutes; // 计划时长（分钟）

    @Column(name = "materials", columnDefinition = "TEXT")
    private String materials; // 所需材料

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions; // 活动说明

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // 活动笔记

    @Column(name = "rating")
    private Integer rating; // 活动评分 1-5

    @Column(name = "completion_percentage", nullable = false)
    private Integer completionPercentage = 0; // 完成百分比

    /**
     * 活动类型枚举
     */
    public enum ActivityType {
        READING("阅读"),
        GAME("游戏"),
        EXERCISE("运动"),
        CRAFT("手工"),
        MUSIC("音乐"),
        OUTDOOR("户外"),
        SOCIAL("社交"),
        LEARNING("学习"),
        CREATIVE("创意"),
        ROUTINE("日常");

        private final String description;

        ActivityType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 活动状态枚举
     */
    public enum ActivityStatus {
        PENDING("待开始"),
        IN_PROGRESS("进行中"),
        COMPLETED("已完成"),
        SKIPPED("已跳过"),
        CANCELLED("已取消");

        private final String description;

        ActivityStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}