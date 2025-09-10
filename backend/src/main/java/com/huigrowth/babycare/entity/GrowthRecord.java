package com.huigrowth.babycare.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 成长记录实体
 * 
 * @author HuiGrowth Team
 */
@Entity
@Table(name = "growth_records", indexes = {
    @Index(name = "idx_growth_record_baby", columnList = "baby_id"),
    @Index(name = "idx_growth_record_type", columnList = "type"),
    @Index(name = "idx_growth_record_created_by", columnList = "created_by"),
    @Index(name = "idx_growth_record_created_at", columnList = "created_at")
})
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"baby", "createdBy"})
@ToString(exclude = {"baby", "createdBy"})
public class GrowthRecord extends BaseEntity {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @NotNull(message = "宝宝不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baby_id", nullable = false)
    private Baby baby;

    @NotNull(message = "记录类型不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RecordType type;

    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 100, message = "标题长度必须在1-100个字符之间")
    @Column(name = "title", nullable = false, length = 100, columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String content;

    @Column(name = "media_urls", columnDefinition = "JSON")
    private String mediaUrls; // JSON格式存储媒体文件URL列表

    @Column(name = "tags", columnDefinition = "JSON")
    private String tags; // JSON格式存储标签列表

    @NotNull(message = "创建者不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "location", length = 100, columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String location; // 记录地点

    @Column(name = "weather", length = 50, columnDefinition = "VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String weather; // 天气状况

    @Column(name = "mood", length = 50, columnDefinition = "VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String mood; // 宝宝心情

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0; // 查看次数

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0; // 点赞次数

    /**
     * 记录类型枚举
     */
    public enum RecordType {
        PHOTO("照片"),
        VIDEO("视频"),
        DIARY("日记"),
        MILESTONE("里程碑"),
        VOICE("语音");

        private final String description;

        RecordType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 获取媒体URL列表
     */
    public List<String> getMediaUrlList() {
        if (mediaUrls == null || mediaUrls.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(mediaUrls, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse media URLs: {}", mediaUrls, e);
            return new ArrayList<>();
        }
    }

    /**
     * 设置媒体URL列表
     */
    public void setMediaUrlList(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            this.mediaUrls = null;
            return;
        }
        try {
            this.mediaUrls = objectMapper.writeValueAsString(urls);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize media URLs: {}", urls, e);
            this.mediaUrls = null;
        }
    }

    /**
     * 获取标签列表
     */
    public List<String> getTagList() {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(tags, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse tags: {}", tags, e);
            return new ArrayList<>();
        }
    }

    /**
     * 设置标签列表
     */
    public void setTagList(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            this.tags = null;
            return;
        }
        try {
            this.tags = objectMapper.writeValueAsString(tags);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize tags: {}", tags, e);
            this.tags = null;
        }
    }
}