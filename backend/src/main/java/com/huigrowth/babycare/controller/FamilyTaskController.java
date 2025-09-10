package com.huigrowth.babycare.controller;

import com.huigrowth.babycare.dto.FamilyTaskCreateRequest;
import com.huigrowth.babycare.dto.FamilyTaskResponse;
import com.huigrowth.babycare.service.FamilyTaskService;
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

/**
 * 家庭任务控制器
 * 
 * @author HuiGrowth Team
 */
@Tag(name = "家庭任务", description = "家庭任务管理接口")
@RestController
@RequestMapping("/family-task")
@RequiredArgsConstructor
@Slf4j
public class FamilyTaskController {

    private final FamilyTaskService familyTaskService;

    @Operation(summary = "创建家庭任务", description = "创建新的家庭任务")
    @PostMapping("/create")
    public ApiResponse<FamilyTaskResponse> createTask(
            @Valid @RequestBody FamilyTaskCreateRequest request,
            Authentication authentication) {
        log.info("创建家庭任务请求: {}", request);
        
        FamilyTaskResponse response = familyTaskService.createTask(
                authentication.getName(), request);
        
        return ApiResponse.success("任务创建成功", response);
    }

    @Operation(summary = "获取家庭任务", description = "分页获取指定家庭的任务")
    @GetMapping("/family/{familyId}")
    public ApiResponse<Page<FamilyTaskResponse>> getFamilyTasks(
            @Parameter(description = "家庭ID") @PathVariable Long familyId,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        log.info("获取家庭任务: familyId={}, page={}, size={}", familyId, page, size);
        
        Page<FamilyTaskResponse> tasks = familyTaskService.getFamilyTasks(
                authentication.getName(), familyId, page, size);
        
        return ApiResponse.success("获取家庭任务成功", tasks);
    }

    @Operation(summary = "获取我的任务", description = "分页获取分配给当前用户的任务")
    @GetMapping("/my-tasks")
    public ApiResponse<Page<FamilyTaskResponse>> getMyTasks(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        log.info("获取我的任务: page={}, size={}", page, size);
        
        Page<FamilyTaskResponse> tasks = familyTaskService.getMyTasks(
                authentication.getName(), page, size);
        
        return ApiResponse.success("获取我的任务成功", tasks);
    }

    @Operation(summary = "开始任务", description = "将任务状态更新为进行中")
    @PostMapping("/{taskId}/start")
    public ApiResponse<FamilyTaskResponse> startTask(
            @Parameter(description = "任务ID") @PathVariable Long taskId,
            Authentication authentication) {
        log.info("开始任务: taskId={}", taskId);
        
        FamilyTaskResponse response = familyTaskService.startTask(
                authentication.getName(), taskId);
        
        return ApiResponse.success("任务开始成功", response);
    }

    @Operation(summary = "完成任务", description = "将任务状态更新为已完成")
    @PostMapping("/{taskId}/complete")
    public ApiResponse<FamilyTaskResponse> completeTask(
            @Parameter(description = "任务ID") @PathVariable Long taskId,
            @Parameter(description = "完成备注") @RequestParam(required = false) String completionNotes,
            Authentication authentication) {
        log.info("完成任务: taskId={}", taskId);
        
        FamilyTaskResponse response = familyTaskService.completeTask(
                authentication.getName(), taskId, completionNotes);
        
        return ApiResponse.success("任务完成成功", response);
    }

    @Operation(summary = "取消任务", description = "将任务状态更新为已取消")
    @PostMapping("/{taskId}/cancel")
    public ApiResponse<FamilyTaskResponse> cancelTask(
            @Parameter(description = "任务ID") @PathVariable Long taskId,
            Authentication authentication) {
        log.info("取消任务: taskId={}", taskId);
        
        FamilyTaskResponse response = familyTaskService.cancelTask(
                authentication.getName(), taskId);
        
        return ApiResponse.success("任务取消成功", response);
    }

    @Operation(summary = "删除任务", description = "删除指定的家庭任务")
    @DeleteMapping("/{taskId}")
    public ApiResponse<String> deleteTask(
            @Parameter(description = "任务ID") @PathVariable Long taskId,
            Authentication authentication) {
        log.info("删除任务: taskId={}", taskId);
        
        familyTaskService.deleteTask(authentication.getName(), taskId);
        
        return ApiResponse.success("任务删除成功");
    }
}