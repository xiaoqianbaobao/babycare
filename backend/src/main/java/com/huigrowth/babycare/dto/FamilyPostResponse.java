package com.huigrowth.babycare.dto;

import com.huigrowth.babycare.entity.FamilyPost;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 家庭动态响应DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class FamilyPostResponse {
    private Long id;
    private Long familyId;
    private String familyName;
    private Long authorId;
    private String authorUsername;
    private String authorNickname;
    private String content;
    private List<String> images;
    private List<String> videos;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private FamilyPost.PostType postType;
    private Boolean isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}