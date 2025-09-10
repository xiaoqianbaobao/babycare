package com.huigrowth.babycare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户资料请求DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class UpdateProfileRequest {

    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;

    @Size(min = 2, max = 10, message = "昵称长度必须在2-10个字符之间")
    private String nickname;

    private String avatar;

    @Size(max = 50, message = "城市名称最多50个字符")
    private String city;
}