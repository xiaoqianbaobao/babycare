package com.huigrowth.babycare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

/**
 * AI聊天会话实体
 * 
 * @author HuiGrowth Team
 */
@Entity
@Table(name = "ai_chat_sessions", indexes = {
    @Index(name = "idx_ai_chat_session_user", columnList = "user_id"),
    @Index(name = "idx_ai_chat_session_baby", columnList = "baby_id"),
    @Index(name = "idx_ai_chat_session_created_at", columnList = "created_at")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"user", "baby", "messages"})
@ToString(exclude = {"user", "baby", "messages"})
public class AIChatSession extends BaseEntity {

    @NotNull(message = "用户不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baby_id")
    private Baby baby; // 可选，如果是针对特定宝宝的咨询

    @NotBlank(message = "会话标题不能为空")
    @Size(min = 1, max = 100, message = "会话标题长度必须在1-100个字符之间")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotNull(message = "咨询类型不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "consultation_type", nullable = false)
    private ConsultationType consultationType;

    @NotNull(message = "会话状态不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary; // 会话摘要

    @Column(name = "tags", length = 500)
    private String tags; // 标签，逗号分隔

    @Column(name = "message_count", nullable = false)
    private Integer messageCount = 0;

    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AIChatMessage> messages;

    /**
     * 咨询类型枚举
     */
    public enum ConsultationType {
        DEVELOPMENT("发育咨询"),
        HEALTH("健康咨询"),
        EDUCATION("教育咨询"),
        BEHAVIOR("行为咨询"),
        NUTRITION("营养咨询"),
        SLEEP("睡眠咨询"),
        SAFETY("安全咨询"),
        PSYCHOLOGY("心理咨询"),
        GENERAL("一般咨询");

        private final String description;

        ConsultationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 会话状态枚举
     */
    public enum SessionStatus {
        ACTIVE("进行中"),
        COMPLETED("已完成"),
        ARCHIVED("已归档");

        private final String description;

        SessionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}