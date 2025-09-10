package com.huigrowth.babycare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 发育评估实体
 * 
 * @author HuiGrowth Team
 */
@Entity
@Table(name = "development_assessments", indexes = {
    @Index(name = "idx_development_assessment_baby", columnList = "baby_id"),
    @Index(name = "idx_development_assessment_category", columnList = "category"),
    @Index(name = "idx_development_assessment_date", columnList = "assessment_date")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"baby"})
@ToString(exclude = {"baby"})
public class DevelopmentAssessment extends BaseEntity {

    @NotNull(message = "宝宝不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baby_id", nullable = false)
    private Baby baby;

    @NotBlank(message = "评估标题不能为空")
    @Size(min = 1, max = 100, message = "评估标题长度必须在1-100个字符之间")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotNull(message = "评估类别不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private AssessmentCategory category;

    @NotNull(message = "评估日期不能为空")
    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;

    @Column(name = "baby_age_months", nullable = false)
    private Integer babyAgeMonths; // 评估时宝宝的月龄

    @Column(name = "questions", columnDefinition = "TEXT")
    private String questions; // JSON格式存储评估问题和答案

    @Column(name = "results", columnDefinition = "TEXT")
    private String results; // JSON格式存储评估结果

    @Column(name = "score", nullable = false)
    private Integer score = 0; // 评估得分

    @Column(name = "max_score", nullable = false)
    private Integer maxScore = 100; // 最高得分

    @NotNull(message = "评估状态不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssessmentStatus status = AssessmentStatus.IN_PROGRESS;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations; // AI生成的建议

    @Column(name = "next_assessment_date")
    private LocalDate nextAssessmentDate; // 下次评估建议日期

    @NotNull(message = "创建者不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * 评估类别枚举
     */
    public enum AssessmentCategory {
        MOTOR_GROSS("大动作发育"),
        MOTOR_FINE("精细动作发育"),
        LANGUAGE("语言发育"),
        COGNITIVE("认知发育"),
        SOCIAL("社交发育"),
        EMOTIONAL("情感发育"),
        ADAPTIVE("适应性发育"),
        COMPREHENSIVE("综合评估");

        private final String description;

        AssessmentCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 评估状态枚举
     */
    public enum AssessmentStatus {
        IN_PROGRESS("进行中"),
        COMPLETED("已完成"),
        CANCELLED("已取消");

        private final String description;

        AssessmentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}