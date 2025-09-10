package com.huigrowth.babycare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录请求DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class LoginRequest {

    @NotBlank(message = "邮箱/用户名不能为空")
    private String emailOrUsername;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String password;

    private Boolean remember = false;
}