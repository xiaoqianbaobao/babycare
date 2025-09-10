package com.huigrowth.babycare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 添加宝宝请求DTO
 * 
 * @author HuiGrowth Team
 */
@Data
public class BabyCreateRequest {

    @NotBlank(message = "宝宝姓名不能为空")
    @Size(min = 1, max = 20, message = "宝宝姓名长度必须在1-20个字符之间")
    private String name;

    @NotNull(message = "宝宝性别不能为空")
    private String gender; // MALE, FEMALE

    @NotNull(message = "宝宝生日不能为空")
    @Past(message = "宝宝生日必须是过去的日期")
    private LocalDate birthday;

    private String avatar;
}