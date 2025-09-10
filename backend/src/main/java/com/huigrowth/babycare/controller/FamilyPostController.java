package com.huigrowth.babycare.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.huigrowth.babycare.dto.FamilyPostCreateRequest;
import com.huigrowth.babycare.dto.FamilyPostResponse;
import com.huigrowth.babycare.service.FamilyPostService;
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
 * 家庭动态控制器
 * 
 * @author HuiGrowth Team
 */
@Tag(name = "家庭动态", description = "家庭动态管理接口")
@RestController
@RequestMapping("/family-post")
@RequiredArgsConstructor
@Slf4j
public class FamilyPostController {

    private final FamilyPostService familyPostService;

    @Operation(summary = "创建家庭动态", description = "发布新的家庭动态")
    @PostMapping("/create")
    public ApiResponse<FamilyPostResponse> createPost(
            @Valid @RequestBody FamilyPostCreateRequest request,
            Authentication authentication) throws JsonProcessingException {
        log.info("创建家庭动态请求: {}", request);
        
        FamilyPostResponse response = familyPostService.createPost(
                authentication.getName(), request);
        
        return ApiResponse.success("动态发布成功", response);
    }

    @Operation(summary = "获取家庭动态", description = "分页获取指定家庭的动态")
    @GetMapping("/family/{familyId}")
    public ApiResponse<Page<FamilyPostResponse>> getFamilyPosts(
            @Parameter(description = "家庭ID") @PathVariable Long familyId,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        log.info("获取家庭动态: familyId={}, page={}, size={}", familyId, page, size);
        
        Page<FamilyPostResponse> posts = familyPostService.getFamilyPosts(
                authentication.getName(), familyId, page, size);
        
        return ApiResponse.success("获取家庭动态成功", posts);
    }

    @Operation(summary = "点赞动态", description = "为家庭动态点赞")
    @PostMapping("/{postId}/like")
    public ApiResponse<FamilyPostResponse> likePost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            Authentication authentication) throws JsonProcessingException {
        log.info("点赞动态: postId={}", postId);
        
        FamilyPostResponse response = familyPostService.likePost(
                authentication.getName(), postId);
        
        return ApiResponse.success("点赞成功", response);
    }

    @Operation(summary = "取消点赞", description = "取消对家庭动态的点赞")
    @DeleteMapping("/{postId}/like")
    public ApiResponse<FamilyPostResponse> unlikePost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            Authentication authentication) throws JsonProcessingException {
        log.info("取消点赞: postId={}", postId);
        
        FamilyPostResponse response = familyPostService.unlikePost(
                authentication.getName(), postId);
        
        return ApiResponse.success("取消点赞成功", response);
    }

    @Operation(summary = "删除动态", description = "删除指定的家庭动态")
    @DeleteMapping("/{postId}")
    public ApiResponse<String> deletePost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            Authentication authentication) {
        log.info("删除动态: postId={}", postId);
        
        familyPostService.deletePost(authentication.getName(), postId);
        
        return ApiResponse.success("动态删除成功");
    }
}