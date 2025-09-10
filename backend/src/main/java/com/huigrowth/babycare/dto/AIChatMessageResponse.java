package com.huigrowth.babycare.dto;

import com.huigrowth.babycare.entity.AIChatMessage;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI聊天消息响应DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class AIChatMessageResponse {
    private Long id;
    private Long chatSessionId;
    private AIChatMessage.MessageType messageType;
    private String content;
    private String metadata;
    private Boolean isHelpful;
    private String feedback;
    private LocalDateTime createdAt;
}