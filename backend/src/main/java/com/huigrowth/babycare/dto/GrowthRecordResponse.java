package com.huigrowth.babycare.dto;

import com.huigrowth.babycare.entity.GrowthRecord;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 成长记录响应DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class GrowthRecordResponse {
    private Long id;
    private Long babyId;
    private String babyName;
    private GrowthRecord.RecordType type;
    private String title;
    private String content;
    private List<String> mediaUrls;
    private List<String> tags;
    private String location;
    private String weather;
    private String mood;
    private Integer viewCount;
    private Integer likeCount;
    private String createdBy;
    private String createdByNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}