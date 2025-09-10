package com.huigrowth.babycare.controller;

import com.huigrowth.babycare.dto.*;
import com.huigrowth.babycare.service.AIParentingService;
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
 * AI育儿助手控制器
 * 
 * @author HuiGrowth Team
 */
@Tag(name = "AI育儿助手", description = "AI智能育儿咨询接口")
@RestController
@RequestMapping("/ai-parenting")
@RequiredArgsConstructor
@Slf4j
public class AIParentingController {

    private final AIParentingService aiParentingService;

    @Operation(summary = "创建聊天会话", description = "创建新的AI育儿咨询会话")
    @PostMapping("/session/create")
    public ApiResponse<AIChatSessionResponse> createSession(
            @Valid @RequestBody AIChatSessionCreateRequest request,
            Authentication authentication) {
        log.info("创建AI聊天会话请求: {}", request);
        
        AIChatSessionResponse response = aiParentingService.createChatSession(
                authentication.getName(), request);
        
        return ApiResponse.success("会话创建成功", response);
    }

    @Operation(summary = "发送消息", description = "向AI发送消息并获取回复")
    @PostMapping("/session/{sessionId}/message")
    public ApiResponse<AIChatMessageResponse> sendMessage(
            @Parameter(description = "会话ID") @PathVariable Long sessionId,
            @Valid @RequestBody AIChatMessageRequest request,
            Authentication authentication) {
        log.info("发送AI消息: sessionId={}, content={}", sessionId, request.getContent());
        
        AIChatMessageResponse response = aiParentingService.sendMessage(
                authentication.getName(), sessionId, request);
        
        return ApiResponse.success("消息发送成功", response);
    }

    @Operation(summary = "获取聊天会话", description = "分页获取用户的聊天会话列表")
    @GetMapping("/sessions")
    public ApiResponse<Page<AIChatSessionResponse>> getSessions(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        log.info("获取用户聊天会话: page={}, size={}", page, size);
        
        Page<AIChatSessionResponse> sessions = aiParentingService.getUserSessions(
                authentication.getName(), page, size);
        
        return ApiResponse.success("获取会话列表成功", sessions);
    }

    @Operation(summary = "获取会话消息", description = "分页获取指定会话的消息历史")
    @GetMapping("/session/{sessionId}/messages")
    public ApiResponse<Page<AIChatMessageResponse>> getSessionMessages(
            @Parameter(description = "会话ID") @PathVariable Long sessionId,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        log.info("获取会话消息: sessionId={}, page={}, size={}", sessionId, page, size);
        
        Page<AIChatMessageResponse> messages = aiParentingService.getSessionMessages(
                authentication.getName(), sessionId, page, size);
        
        return ApiResponse.success("获取消息历史成功", messages);
    }

    @Operation(summary = "评价AI回复", description = "标记AI回复是否有帮助")
    @PostMapping("/message/{messageId}/feedback")
    public ApiResponse<Void> markMessageHelpful(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "是否有帮助") @RequestParam boolean helpful,
            @Parameter(description = "反馈内容") @RequestParam(required = false) String feedback,
            Authentication authentication) {
        log.info("评价AI回复: messageId={}, helpful={}", messageId, helpful);
        
        aiParentingService.markMessageHelpful(
                authentication.getName(), messageId, helpful, feedback);
        
        return ApiResponse.success();
    }

    @Operation(summary = "完成会话", description = "完成聊天会话并添加摘要")
    @PostMapping("/session/{sessionId}/complete")
    public ApiResponse<AIChatSessionResponse> completeSession(
            @Parameter(description = "会话ID") @PathVariable Long sessionId,
            @Parameter(description = "会话摘要") @RequestParam(required = false) String summary,
            Authentication authentication) {
        log.info("完成会话: sessionId={}", sessionId);
        
        AIChatSessionResponse response = aiParentingService.completeSession(
                authentication.getName(), sessionId, summary);
        
        return ApiResponse.success("会话完成", response);
    }
}