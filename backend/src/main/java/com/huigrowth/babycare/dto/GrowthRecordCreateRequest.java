package com.huigrowth.babycare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建成长记录请求DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class GrowthRecordCreateRequest {

    @NotNull(message = "宝宝ID不能为空")
    private Long babyId;

    @NotBlank(message = "记录类型不能为空")
    private String type; // PHOTO, VIDEO, DIARY, MILESTONE

    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 100, message = "标题长度必须在1-100个字符之间")
    private String title;

    @Size(max = 2000, message = "内容长度不能超过2000个字符")
    private String content;

    private List<String> mediaUrls;

    private List<String> tags;
}