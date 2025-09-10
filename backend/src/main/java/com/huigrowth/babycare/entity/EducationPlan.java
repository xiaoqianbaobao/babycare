package com.huigrowth.babycare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;

/**
 * 教育计划实体
 * 
 * @author HuiGrowth Team
 */
@Entity
@Table(name = "education_plans", indexes = {
    @Index(name = "idx_education_plan_baby", columnList = "baby_id"),
    @Index(name = "idx_education_plan_category", columnList = "category"),
    @Index(name = "idx_education_plan_status", columnList = "status"),
    @Index(name = "idx_education_plan_start_date", columnList = "start_date")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"baby", "createdBy", "activities"})
@ToString(exclude = {"baby", "createdBy", "activities"})
public class EducationPlan extends BaseEntity {

    @NotNull(message = "宝宝不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baby_id", nullable = false)
    private Baby baby;

    @NotBlank(message = "计划名称不能为空")
    @Size(min = 1, max = 100, message = "计划名称长度必须在1-100个字符之间")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "教育类别不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private EducationCategory category;

    @NotNull(message = "计划状态不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlanStatus status = PlanStatus.DRAFT;

    @NotNull(message = "开始日期不能为空")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @NotNull(message = "目标年龄不能为空")
    @Column(name = "target_age_months", nullable = false)
    private Integer targetAgeMonths; // 目标年龄（月）

    @Column(name = "difficulty_level")
    private Integer difficultyLevel = 1; // 难度等级 1-5

    @Column(name = "goals", columnDefinition = "TEXT")
    private String goals; // 学习目标

    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage = 0; // 进度百分比

    @NotNull(message = "创建者不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "educationPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<EducationActivity> activities;

    /**
     * 教育类别枚举
     */
    public enum EducationCategory {
        COGNITIVE("认知发展"),
        LANGUAGE("语言发展"),
        MOTOR("运动发展"),
        SOCIAL("社交发展"),
        EMOTIONAL("情感发展"),
        CREATIVE("创造力"),
        MUSIC("音乐启蒙"),
        ART("艺术启蒙"),
        READING("阅读启蒙"),
        MATH("数学启蒙");

        private final String description;

        EducationCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 计划状态枚举
     */
    public enum PlanStatus {
        DRAFT("草稿"),
        ACTIVE("进行中"),
        PAUSED("暂停"),
        COMPLETED("已完成"),
        CANCELLED("已取消");

        private final String description;

        PlanStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}