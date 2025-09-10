package com.huigrowth.babycare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建AI聊天会话请求DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class AIChatSessionCreateRequest {

    @NotBlank(message = "会话标题不能为空")
    @Size(min = 1, max = 100, message = "会话标题长度必须在1-100个字符之间")
    private String title;

    @NotBlank(message = "咨询类型不能为空")
    private String consultationType; // DEVELOPMENT, HEALTH, EDUCATION, BEHAVIOR, NUTRITION, SLEEP, SAFETY, PSYCHOLOGY, GENERAL

    private Long babyId; // 可选，如果是针对特定宝宝的咨询

    @Size(max = 500, message = "标签长度不能超过500个字符")
    private String tags;
}