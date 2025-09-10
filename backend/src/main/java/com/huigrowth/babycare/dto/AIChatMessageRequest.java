package com.huigrowth.babycare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送AI聊天消息请求DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class AIChatMessageRequest {

    @NotBlank(message = "消息内容不能为空")
    private String content;

    private String context; // 额外的上下文信息
}