package com.huigrowth.babycare.service;

import com.huigrowth.babycare.dto.*;
import com.huigrowth.babycare.entity.*;
import com.huigrowth.babycare.exception.BusinessException;
import com.huigrowth.babycare.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI育儿助手服务
 * 
 * @author HuiGrowth Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIParentingService {

    private final AIChatSessionRepository chatSessionRepository;
    private final AIChatMessageRepository chatMessageRepository;
    private final BabyRepository babyRepository;
    private final UserRepository userRepository;

    /**
     * 创建AI聊天会话
     */
    @Transactional
    public AIChatSessionResponse createChatSession(String username, AIChatSessionCreateRequest request) {
        log.info("创建AI聊天会话: username={}, request={}", username, request);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找宝宝（如果指定）
        Baby baby = null;
        if (request.getBabyId() != null) {
            baby = babyRepository.findById(request.getBabyId())
                    .orElseThrow(() -> new BusinessException("宝宝不存在"));
            
            // 验证用户是否有权限访问该宝宝
            if (!hasAccessToBaby(user, baby)) {
                throw new BusinessException("您没有权限为该宝宝创建咨询会话");
            }
        }

        // 创建会话
        AIChatSession session = new AIChatSession();
        session.setUser(user);
        session.setBaby(baby);
        session.setTitle(request.getTitle());
        session.setConsultationType(AIChatSession.ConsultationType.valueOf(request.getConsultationType()));
        session.setTags(request.getTags());

        AIChatSession savedSession = chatSessionRepository.save(session);
        log.info("成功创建AI聊天会话: id={}", savedSession.getId());

        return convertToSessionResponse(savedSession);
    }

    /**
     * 发送消息并获取AI回复
     */
    @Transactional
    public AIChatMessageResponse sendMessage(String username, Long sessionId, AIChatMessageRequest request) {
        log.info("发送AI聊天消息: username={}, sessionId={}, content={}", username, sessionId, request.getContent());

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找会话
        AIChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException("聊天会话不存在"));

        // 验证权限
        if (!session.getUser().getId().equals(user.getId())) {
            throw new BusinessException("您没有权限访问该会话");
        }

        // 保存用户消息
        AIChatMessage userMessage = new AIChatMessage();
        userMessage.setChatSession(session);
        userMessage.setMessageType(AIChatMessage.MessageType.USER);
        userMessage.setContent(request.getContent());
        chatMessageRepository.save(userMessage);

        // 生成AI回复
        String aiReply = generateAIReply(request.getContent(), session);
        
        // 保存AI消息
        AIChatMessage aiMessage = new AIChatMessage();
        aiMessage.setChatSession(session);
        aiMessage.setMessageType(AIChatMessage.MessageType.AI);
        aiMessage.setContent(aiReply);
        aiMessage.setMetadata(String.format("{\"response_time\":%d,\"confidence\":0.85}", System.currentTimeMillis()));
        AIChatMessage savedAiMessage = chatMessageRepository.save(aiMessage);

        // 更新会话消息计数
        session.setMessageCount(session.getMessageCount() + 2);
        chatSessionRepository.save(session);

        return convertToMessageResponse(savedAiMessage);
    }

    /**
     * 获取用户的聊天会话
     */
    public Page<AIChatSessionResponse> getUserSessions(String username, int page, int size) {
        log.info("获取用户聊天会话: username={}, page={}, size={}", username, page, size);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        Pageable pageable = PageRequest.of(page, size);
        Page<AIChatSession> sessions = chatSessionRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return sessions.map(this::convertToSessionResponse);
    }

    /**
     * 获取会话的消息历史
     */
    public Page<AIChatMessageResponse> getSessionMessages(String username, Long sessionId, int page, int size) {
        log.info("获取会话消息: username={}, sessionId={}, page={}, size={}", username, sessionId, page, size);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找会话
        AIChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException("聊天会话不存在"));

        // 验证权限
        if (!session.getUser().getId().equals(user.getId())) {
            throw new BusinessException("您没有权限访问该会话");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<AIChatMessage> messages = chatMessageRepository.findByChatSessionOrderByCreatedAtAsc(session, pageable);

        return messages.map(this::convertToMessageResponse);
    }

    /**
     * 标记AI回复是否有帮助
     */
    @Transactional
    public void markMessageHelpful(String username, Long messageId, boolean helpful, String feedback) {
        log.info("标记消息帮助程度: username={}, messageId={}, helpful={}", username, messageId, helpful);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找消息
        AIChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException("消息不存在"));

        // 验证权限
        if (!message.getChatSession().getUser().getId().equals(user.getId())) {
            throw new BusinessException("您没有权限操作该消息");
        }

        // 只能标记AI消息
        if (message.getMessageType() != AIChatMessage.MessageType.AI) {
            throw new BusinessException("只能对AI回复进行评价");
        }

        message.setIsHelpful(helpful);
        message.setFeedback(feedback);
        chatMessageRepository.save(message);
    }

    /**
     * 完成会话
     */
    @Transactional
    public AIChatSessionResponse completeSession(String username, Long sessionId, String summary) {
        log.info("完成会话: username={}, sessionId={}", username, sessionId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找会话
        AIChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException("聊天会话不存在"));

        // 验证权限
        if (!session.getUser().getId().equals(user.getId())) {
            throw new BusinessException("您没有权限操作该会话");
        }

        session.setStatus(AIChatSession.SessionStatus.COMPLETED);
        session.setSummary(summary);
        AIChatSession savedSession = chatSessionRepository.save(session);

        return convertToSessionResponse(savedSession);
    }

    /**
     * 生成AI回复（模拟实现）
     */
    private String generateAIReply(String userMessage, AIChatSession session) {
        // 这里是模拟的AI回复逻辑，实际项目中应该调用真实的AI服务
        String message = userMessage.toLowerCase();
        
        if (message.contains("睡眠") || message.contains("睡觉")) {
            return "关于宝宝的睡眠问题，我建议：\n1. 建立规律的睡眠时间\n2. 创造舒适的睡眠环境\n3. 睡前进行安静的活动\n4. 避免过度刺激\n\n如果问题持续，建议咨询儿科医生。";
        } else if (message.contains("喂养") || message.contains("吃奶") || message.contains("辅食")) {
            return "关于宝宝的喂养，需要根据月龄来调整：\n1. 0-6个月：纯母乳或配方奶\n2. 6个月后：逐步添加辅食\n3. 注意营养均衡\n4. 观察宝宝的反应\n\n具体的喂养计划建议咨询儿科医生制定。";
        } else if (message.contains("发育") || message.contains("成长")) {
            return "宝宝的发育是一个渐进的过程：\n1. 每个宝宝的发育节奏都不同\n2. 关注关键的发育里程碑\n3. 提供适当的刺激和环境\n4. 定期进行发育评估\n\n如果您担心宝宝的发育情况，建议进行专业的发育评估。";
        } else if (message.contains("哭闹") || message.contains("哭")) {
            return "宝宝哭闹的常见原因包括：\n1. 饥饿或口渴\n2. 需要换尿布\n3. 感到不适或疼痛\n4. 需要安慰和陪伴\n5. 过度疲劳\n\n建议先检查基本需求，然后尝试安抚方法。如果哭闹异常持续，请及时就医。";
        } else if (message.contains("教育") || message.contains("学习")) {
            return "早期教育的重点是：\n1. 通过游戏促进学习\n2. 读书给宝宝听\n3. 鼓励探索和好奇心\n4. 提供丰富的感官体验\n5. 保持耐心和积极的态度\n\n记住，玩耍就是宝宝最好的学习方式！";
        } else {
            return "感谢您的提问！作为AI育儿助手，我会尽力为您提供专业的建议。\n\n如果您有具体的育儿问题，比如关于宝宝的睡眠、喂养、发育、教育等方面，请详细描述情况，我会给出更针对性的建议。\n\n请注意，我的建议仅供参考，如果遇到严重问题，请及时咨询专业医生。";
        }
    }

    /**
     * 检查用户是否有权限访问宝宝
     */
    private boolean hasAccessToBaby(User user, Baby baby) {
        return baby.getFamily().getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()) && member.getActive());
    }

    /**
     * 转换为会话响应DTO
     */
    private AIChatSessionResponse convertToSessionResponse(AIChatSession session) {
        AIChatSessionResponse response = new AIChatSessionResponse();
        response.setId(session.getId());
        response.setTitle(session.getTitle());
        response.setConsultationType(session.getConsultationType());
        response.setStatus(session.getStatus());
        response.setSummary(session.getSummary());
        response.setTags(session.getTags());
        response.setMessageCount(session.getMessageCount());
        response.setUsername(session.getUser().getUsername());
        response.setCreatedAt(session.getCreatedAt());
        response.setUpdatedAt(session.getUpdatedAt());
        
        if (session.getBaby() != null) {
            response.setBabyId(session.getBaby().getId());
            response.setBabyName(session.getBaby().getName());
        }

        // 获取最后一条消息
        List<AIChatMessage> recentMessages = chatMessageRepository.findRecentMessages(session, PageRequest.of(0, 1));
        if (!recentMessages.isEmpty()) {
            AIChatMessage lastMessage = recentMessages.get(0);
            response.setLastMessage(lastMessage.getContent());
            response.setLastMessageTime(lastMessage.getCreatedAt());
        }

        return response;
    }

    /**
     * 转换为消息响应DTO
     */
    private AIChatMessageResponse convertToMessageResponse(AIChatMessage message) {
        AIChatMessageResponse response = new AIChatMessageResponse();
        response.setId(message.getId());
        response.setChatSessionId(message.getChatSession().getId());
        response.setMessageType(message.getMessageType());
        response.setContent(message.getContent());
        response.setMetadata(message.getMetadata());
        response.setIsHelpful(message.getIsHelpful());
        response.setFeedback(message.getFeedback());
        response.setCreatedAt(message.getCreatedAt());

        return response;
    }
}