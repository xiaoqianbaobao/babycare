package com.huigrowth.babycare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;

/**
 * 家庭实体
 * 
 * @author HuiGrowth Team
 */
@Entity
@Table(name = "families", indexes = {
    @Index(name = "idx_family_invite_code", columnList = "inviteCode")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"members", "babies"})
@ToString(exclude = {"members", "babies"})
public class Family extends BaseEntity {

    @NotBlank(message = "家庭名称不能为空")
    @Size(min = 2, max = 20, message = "家庭名称长度必须在2-20个字符之间")
    @Column(name = "name", nullable = false, length = 20, columnDefinition = "VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String name;

    @Column(name = "invite_code", unique = true, nullable = false, length = 32, columnDefinition = "VARCHAR(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String inviteCode;

    @Column(name = "description", length = 200, columnDefinition = "VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String description;

    @Column(name = "avatar", length = 500, columnDefinition = "VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String avatar;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FamilyMember> members;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Baby> babies;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (inviteCode == null) {
            inviteCode = generateInviteCode();
        }
    }

    /**
     * 生成邀请码
     */
    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}