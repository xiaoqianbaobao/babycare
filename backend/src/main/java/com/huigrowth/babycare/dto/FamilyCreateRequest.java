package com.huigrowth.babycare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建家庭请求DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class FamilyCreateRequest {

    @NotBlank(message = "家庭名称不能为空")
    @Size(min = 2, max = 50, message = "家庭名称长度必须在2-50个字符之间")
    private String name;

    @Size(max = 200, message = "家庭描述长度不能超过200个字符")
    private String description;
}