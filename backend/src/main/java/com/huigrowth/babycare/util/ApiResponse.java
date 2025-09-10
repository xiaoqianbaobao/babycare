package com.huigrowth.babycare.util;

import lombok.Data;

/**
 * 统一API响应格式
 * 
 * @author HuiGrowth Team
 */
@Data
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String code;
    
    private ApiResponse() {}
    
    private ApiResponse(boolean success, String message, T data, String code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, "操作成功", null, "SUCCESS");
    }
    
    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data, "SUCCESS");
    }
    
    /**
     * 成功响应（带消息和数据）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, "SUCCESS");
    }
    
    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, "ERROR");
    }
    
    /**
     * 失败响应（带错误码）
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, message, null, code);
    }
    
    /**
     * 失败响应（带错误码和数据）
     */
    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return new ApiResponse<>(false, message, data, code);
    }
}