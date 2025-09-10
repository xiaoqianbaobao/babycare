package com.huigrowth.babycare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建家庭动态请求DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class FamilyPostCreateRequest {

    @NotNull(message = "家庭ID不能为空")
    private Long familyId;

    @NotBlank(message = "动态内容不能为空")
    @Size(min = 1, max = 1000, message = "内容长度必须在1-1000个字符之间")
    private String content;

    private List<String> images; // 图片URL列表

    private List<String> videos; // 视频URL列表
    
    // Explicitly adding getter method to resolve compilation issue
    public Long getFamilyId() {
        return familyId;
    }
    
    public String getContent() {
        return content;
    }
    
    public List<String> getImages() {
        return images;
    }
    
    public List<String> getVideos() {
        return videos;
    }
}