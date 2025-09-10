package com.huigrowth.babycare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JWT响应DTO
 * 
 * @author HuiGrowth Team
 */
@Data
@AllArgsConstructor
public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private UserResponse user;

    public JwtResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }
}