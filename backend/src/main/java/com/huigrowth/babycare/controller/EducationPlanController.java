package com.huigrowth.babycare.controller;

import com.huigrowth.babycare.dto.*;
import com.huigrowth.babycare.service.EducationPlanService;
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
 * 教育计划控制器
 * 
 * @author HuiGrowth Team
 */
@Tag(name = "教育计划", description = "教育计划管理接口")
@RestController
@RequestMapping("/education-plan")
@RequiredArgsConstructor
@Slf4j
public class EducationPlanController {

    private final EducationPlanService educationPlanService;

    @Operation(summary = "创建教育计划", description = "为宝宝创建新的教育计划")
    @PostMapping("/create")
    public ApiResponse<EducationPlanResponse> createPlan(
            @Valid @RequestBody EducationPlanCreateRequest request,
            Authentication authentication) {
        log.info("创建教育计划请求: {}", request);
        
        EducationPlanResponse response = educationPlanService.createPlan(
                authentication.getName(), request);
        
        return ApiResponse.success("教育计划创建成功", response);
    }

    @Operation(summary = "获取宝宝教育计划", description = "分页获取指定宝宝的教育计划")
    @GetMapping("/baby/{babyId}")
    public ApiResponse<Page<EducationPlanResponse>> getBabyPlans(
            @Parameter(description = "宝宝ID") @PathVariable Long babyId,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        log.info("获取宝宝教育计划: babyId={}, page={}, size={}", babyId, page, size);
        
        Page<EducationPlanResponse> plans = educationPlanService.getBabyPlans(
                authentication.getName(), babyId, page, size);
        
        return ApiResponse.success("获取教育计划成功", plans);
    }

    @Operation(summary = "获取进行中的计划", description = "获取宝宝当前进行中的教育计划")
    @GetMapping("/baby/{babyId}/active")
    public ApiResponse<List<EducationPlanResponse>> getActivePlans(
            @Parameter(description = "宝宝ID") @PathVariable Long babyId,
            Authentication authentication) {
        log.info("获取进行中的教育计划: babyId={}", babyId);
        
        List<EducationPlanResponse> plans = educationPlanService.getActivePlans(
                authentication.getName(), babyId);
        
        return ApiResponse.success("获取进行中计划成功", plans);
    }

    @Operation(summary = "启动计划", description = "启动教育计划")
    @PostMapping("/{planId}/start")
    public ApiResponse<EducationPlanResponse> startPlan(
            @Parameter(description = "计划ID") @PathVariable Long planId,
            Authentication authentication) {
        log.info("启动教育计划: planId={}", planId);
        
        EducationPlanResponse response = educationPlanService.startPlan(
                authentication.getName(), planId);
        
        return ApiResponse.success("计划启动成功", response);
    }

    @Operation(summary = "完成计划", description = "完成教育计划")
    @PostMapping("/{planId}/complete")
    public ApiResponse<EducationPlanResponse> completePlan(
            @Parameter(description = "计划ID") @PathVariable Long planId,
            Authentication authentication) {
        log.info("完成教育计划: planId={}", planId);
        
        EducationPlanResponse response = educationPlanService.completePlan(
                authentication.getName(), planId);
        
        return ApiResponse.success("计划完成成功", response);
    }

    @Operation(summary = "创建教育活动", description = "为教育计划创建新的活动")
    @PostMapping("/activity/create")
    public ApiResponse<EducationActivityResponse> createActivity(
            @Valid @RequestBody EducationActivityCreateRequest request,
            Authentication authentication) {
        log.info("创建教育活动请求: {}", request);
        
        EducationActivityResponse response = educationPlanService.createActivity(
                authentication.getName(), request);
        
        return ApiResponse.success("教育活动创建成功", response);
    }

    @Operation(summary = "获取计划活动", description = "分页获取教育计划的活动")
    @GetMapping("/{planId}/activities")
    public ApiResponse<Page<EducationActivityResponse>> getPlanActivities(
            @Parameter(description = "计划ID") @PathVariable Long planId,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        log.info("获取计划活动: planId={}, page={}, size={}", planId, page, size);
        
        Page<EducationActivityResponse> activities = educationPlanService.getPlanActivities(
                authentication.getName(), planId, page, size);
        
        return ApiResponse.success("获取计划活动成功", activities);
    }

    @Operation(summary = "完成活动", description = "完成教育活动")
    @PostMapping("/activity/{activityId}/complete")
    public ApiResponse<EducationActivityResponse> completeActivity(
            @Parameter(description = "活动ID") @PathVariable Long activityId,
            @Parameter(description = "活动笔记") @RequestParam(required = false) String notes,
            @Parameter(description = "活动评分 1-5") @RequestParam(required = false) Integer rating,
            Authentication authentication) {
        log.info("完成教育活动: activityId={}, rating={}", activityId, rating);
        
        EducationActivityResponse response = educationPlanService.completeActivity(
                authentication.getName(), activityId, notes, rating);
        
        return ApiResponse.success("活动完成成功", response);
    }
}