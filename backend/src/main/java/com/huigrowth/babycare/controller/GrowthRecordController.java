package com.huigrowth.babycare.controller;

import com.huigrowth.babycare.dto.GrowthRecordCreateRequest;
import com.huigrowth.babycare.dto.GrowthRecordResponse;
import com.huigrowth.babycare.service.GrowthRecordService;
import com.huigrowth.babycare.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 成长记录控制器
 * 
 * @author HuiGrowth Team
 */
@Tag(name = "成长记录", description = "成长记录管理接口")
@RestController
@RequestMapping("/growth-record")
@RequiredArgsConstructor
@Slf4j
public class GrowthRecordController {

    private final GrowthRecordService growthRecordService;

    @Operation(summary = "创建成长记录", description = "为宝宝创建新的成长记录")
    @PostMapping("/create")
    public ApiResponse<GrowthRecordResponse> createRecord(
            @Valid @RequestBody GrowthRecordCreateRequest request,
            Authentication authentication) {
        log.info("创建成长记录请求: {}", request);
        
        GrowthRecordResponse response = growthRecordService.createRecord(
                authentication.getName(), request);
        
        return ApiResponse.success("成长记录创建成功", response);
    }

    @Operation(summary = "获取宝宝成长记录", description = "分页获取指定宝宝的成长记录")
    @GetMapping("/baby/{babyId}")
    public ApiResponse<Page<GrowthRecordResponse>> getBabyRecords(
            @Parameter(description = "宝宝ID") @PathVariable Long babyId,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        log.info("获取宝宝成长记录: babyId={}, page={}, size={}", babyId, page, size);
        
        Page<GrowthRecordResponse> records = growthRecordService.getBabyRecords(
                authentication.getName(), babyId, page, size);
        
        return ApiResponse.success("获取成长记录成功", records);
    }

    @Operation(summary = "按类型获取记录", description = "获取指定类型的成长记录")
    @GetMapping("/baby/{babyId}/type/{type}")
    public ApiResponse<List<GrowthRecordResponse>> getRecordsByType(
            @Parameter(description = "宝宝ID") @PathVariable Long babyId,
            @Parameter(description = "记录类型：PHOTO, VIDEO, DIARY, MILESTONE, VOICE") @PathVariable String type,
            Authentication authentication) {
        log.info("按类型获取成长记录: babyId={}, type={}", babyId, type);
        
        List<GrowthRecordResponse> records = growthRecordService.getRecordsByType(
                authentication.getName(), babyId, type);
        
        return ApiResponse.success("获取记录成功", records);
    }

    @Operation(summary = "搜索成长记录", description = "根据关键词搜索成长记录")
    @GetMapping("/baby/{babyId}/search")
    public ApiResponse<List<GrowthRecordResponse>> searchRecords(
            @Parameter(description = "宝宝ID") @PathVariable Long babyId,
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            Authentication authentication) {
        log.info("搜索成长记录: babyId={}, keyword={}", babyId, keyword);
        
        List<GrowthRecordResponse> records = growthRecordService.searchRecords(
                authentication.getName(), babyId, keyword);
        
        return ApiResponse.success("搜索完成", records);
    }

    @Operation(summary = "获取最近记录", description = "获取用户最近的成长记录")
    @GetMapping("/recent")
    public ApiResponse<List<GrowthRecordResponse>> getRecentRecords(
            @Parameter(description = "返回记录数量") @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        log.info("获取最近记录: limit={}", limit);
        
        List<GrowthRecordResponse> records = growthRecordService.getRecentRecords(
                authentication.getName(), limit);
        
        return ApiResponse.success("获取最近记录成功", records);
    }

    @Operation(summary = "记录查看", description = "增加记录的查看次数")
    @PostMapping("/{recordId}/view")
    public ApiResponse<Void> viewRecord(
            @Parameter(description = "记录ID") @PathVariable Long recordId) {
        log.info("记录查看: recordId={}", recordId);
        
        growthRecordService.incrementViewCount(recordId);
        
        return ApiResponse.success();
    }
}