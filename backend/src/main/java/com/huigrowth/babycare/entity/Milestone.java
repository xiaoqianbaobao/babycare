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
 * 里程碑实体
 * 
 * @author HuiGrowth Team
 */
@Entity
@Table(name = "milestones", indexes = {
    @Index(name = "idx_milestone_baby", columnList = "baby_id"),
    @Index(name = "idx_milestone_category", columnList = "category"),
    @Index(name = "idx_milestone_achieved_at", columnList = "achieved_at")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"baby"})
@ToString(exclude = {"baby"})
public class Milestone extends BaseEntity {

    @NotNull(message = "宝宝不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baby_id", nullable = false)
    private Baby baby;

    @NotBlank(message = "里程碑标题不能为空")
    @Size(min = 1, max = 100, message = "标题长度必须在1-100个字符之间")
    @Column(name = "title", nullable = false, length = 100, columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String title;

    @Column(name = "description", length = 500, columnDefinition = "VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String description;

    @NotNull(message = "达成时间不能为空")
    @Column(name = "achieved_at", nullable = false)
    private LocalDateTime achievedAt;

    @NotNull(message = "里程碑类别不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private MilestoneCategory category;

    @Column(name = "photos", columnDefinition = "JSON")
    private String photos; // JSON格式存储照片URL列表

    @Column(name = "age_in_months")
    private Integer ageInMonths; // 达成时的月龄

    @Column(name = "age_in_days")
    private Integer ageInDays; // 达成时的日龄

    @Column(name = "is_preset", nullable = false)
    private Boolean isPreset = false; // 是否为预设里程碑

    @Column(name = "preset_milestone_id")
    private Long presetMilestoneId; // 预设里程碑ID

    @Column(name = "celebration_message", length = 200, columnDefinition = "VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String celebrationMessage; // 庆祝信息

    /**
     * 里程碑类别枚举
     */
    public enum MilestoneCategory {
        MOTOR("大运动"),
        FINE_MOTOR("精细动作"),
        LANGUAGE("语言发展"),
        COGNITIVE("认知能力"),
        SOCIAL("社交情感"),
        SELF_CARE("生活自理"),
        OTHER("其他");

        private final String description;

        MilestoneCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}