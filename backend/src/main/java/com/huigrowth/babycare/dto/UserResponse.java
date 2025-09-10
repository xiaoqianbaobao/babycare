package com.huigrowth.babycare.dto;

import com.huigrowth.babycare.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String nickname;
    private String avatar;
    private String city;
    private User.UserRole role;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setCity(user.getCity());
        response.setRole(user.getRole());
        response.setEmailVerified(user.getEmailVerified());
        response.setPhoneVerified(user.getPhoneVerified());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}