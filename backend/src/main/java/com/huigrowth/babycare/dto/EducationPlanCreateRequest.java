package com.huigrowth.babycare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建教育计划请求DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class EducationPlanCreateRequest {

    @NotNull(message = "宝宝ID不能为空")
    private Long babyId;

    @NotBlank(message = "计划名称不能为空")
    @Size(min = 1, max = 100, message = "计划名称长度必须在1-100个字符之间")
    private String name;

    @Size(max = 2000, message = "描述长度不能超过2000个字符")
    private String description;

    @NotBlank(message = "教育类别不能为空")
    private String category; // COGNITIVE, LANGUAGE, MOTOR, SOCIAL, EMOTIONAL, CREATIVE, MUSIC, ART, READING, MATH

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "目标年龄不能为空")
    private Integer targetAgeMonths;

    private Integer difficultyLevel = 1; // 1-5

    @Size(max = 2000, message = "学习目标长度不能超过2000个字符")
    private String goals;
}