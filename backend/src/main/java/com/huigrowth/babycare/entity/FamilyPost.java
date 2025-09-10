package com.huigrowth.babycare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 家庭动态实体
 * 
 * @author HuiGrowth Team
 */
@Entity
@Table(name = "family_posts", indexes = {
    @Index(name = "idx_family_post_family", columnList = "family_id"),
    @Index(name = "idx_family_post_author", columnList = "author_id"),
    @Index(name = "idx_family_post_created_at", columnList = "created_at")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"family", "author"})
@ToString(exclude = {"family", "author"})
public class FamilyPost extends BaseEntity {

    @NotNull(message = "家庭不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @NotNull(message = "作者不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @NotBlank(message = "动态内容不能为空")
    @Size(min = 1, max = 1000, message = "内容长度必须在1-1000个字符之间")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "images", columnDefinition = "JSON")
    private String images; // JSON格式存储图片URL列表

    @Column(name = "videos", columnDefinition = "JSON")
    private String videos; // JSON格式存储视频URL列表

    @Column(name = "likes", columnDefinition = "JSON")
    private String likes; // JSON格式存储点赞用户ID列表

    @Column(name = "comments", columnDefinition = "JSON")
    private String comments; // JSON格式存储评论列表

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType = PostType.GENERAL;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false; // 是否置顶

    @Column(name = "visibility", nullable = false)
    private Integer visibility = 0; // 可见性：0-家庭内可见，1-公开

    /**
     * 动态类型枚举
     */
    public enum PostType {
        GENERAL("一般动态"),
        MILESTONE("里程碑分享"),
        PHOTO_ALBUM("相册分享"),
        QUESTION("问题求助"),
        EXPERIENCE("经验分享"),
        ANNOUNCEMENT("公告通知");

        private final String description;

        PostType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}