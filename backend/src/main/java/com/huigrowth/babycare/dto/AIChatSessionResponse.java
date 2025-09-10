package com.huigrowth.babycare.dto;

import com.huigrowth.babycare.entity.AIChatSession;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI聊天会话响应DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class AIChatSessionResponse {
    private Long id;
    private String title;
    private AIChatSession.ConsultationType consultationType;
    private AIChatSession.SessionStatus status;
    private String summary;
    private String tags;
    private Integer messageCount;
    private Long babyId;
    private String babyName;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String lastMessage; // 最后一条消息的内容
    private LocalDateTime lastMessageTime;
}