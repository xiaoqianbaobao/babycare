package com.huigrowth.babycare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建教育活动请求DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class EducationActivityCreateRequest {

    @NotNull(message = "教育计划ID不能为空")
    private Long educationPlanId;

    @NotBlank(message = "活动名称不能为空")
    @Size(min = 1, max = 100, message = "活动名称长度必须在1-100个字符之间")
    private String name;

    @Size(max = 2000, message = "描述长度不能超过2000个字符")
    private String description;

    @NotBlank(message = "活动类型不能为空")
    private String type; // READING, GAME, EXERCISE, CRAFT, MUSIC, OUTDOOR, SOCIAL, LEARNING, CREATIVE, ROUTINE

    private LocalDateTime scheduledTime;

    private Integer durationMinutes;

    @Size(max = 2000, message = "所需材料长度不能超过2000个字符")
    private String materials;

    @Size(max = 2000, message = "活动说明长度不能超过2000个字符")
    private String instructions;
}