package com.huigrowth.babycare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * AI聊天消息实体
 * 
 * @author HuiGrowth Team
 */
@Entity
@Table(name = "ai_chat_messages", indexes = {
    @Index(name = "idx_ai_chat_message_session", columnList = "chat_session_id"),
    @Index(name = "idx_ai_chat_message_created_at", columnList = "created_at")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"chatSession"})
@ToString(exclude = {"chatSession"})
public class AIChatMessage extends BaseEntity {

    @NotNull(message = "聊天会话不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_session_id", nullable = false)
    private AIChatSession chatSession;

    @NotNull(message = "消息类型不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @NotBlank(message = "消息内容不能为空")
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON格式存储额外信息，如AI响应时间、置信度等

    @Column(name = "is_helpful")
    private Boolean isHelpful; // 用户是否认为这条AI回复有帮助

    @Column(name = "feedback", length = 500)
    private String feedback; // 用户反馈

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        USER("用户消息"),
        AI("AI回复"),
        SYSTEM("系统消息");

        private final String description;

        MessageType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}