package com.huigrowth.babycare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * AI聊天会话实体
 * 
 * @author HuiGrowth Team
 */
@Entity
@Table(name = "ai_chats", indexes = {
    @Index(name = "idx_ai_chat_user", columnList = "user_id"),
    @Index(name = "idx_ai_chat_baby", columnList = "baby_id"),
    @Index(name = "idx_ai_chat_created_at", columnList = "created_at")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"user", "baby", "messages"})
@ToString(exclude = {"user", "baby", "messages"})
public class AIChat extends BaseEntity {

    @NotNull(message = "用户不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baby_id")
    private Baby baby;

    @Column(name = "topic", length = 100)
    private String topic; // 聊天主题

    @Column(name = "messages", columnDefinition = "JSON")
    private String messages; // JSON格式存储消息列表

    @Column(name = "message_count", nullable = false)
    private Integer messageCount = 0; // 消息数量

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // 是否活跃

    @Column(name = "session_id", length = 100)
    private String sessionId; // 会话ID
}