package com.huigrowth.babycare.repository;

import com.huigrowth.babycare.entity.AIChatMessage;
import com.huigrowth.babycare.entity.AIChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI聊天消息数据访问层
 * 
 * @author HuiGrowth Team
 */
@Repository
public interface AIChatMessageRepository extends JpaRepository<AIChatMessage, Long> {

    /**
     * 查找会话的消息
     */
    Page<AIChatMessage> findByChatSessionOrderByCreatedAtAsc(AIChatSession chatSession, Pageable pageable);

    /**
     * 按类型查找会话的消息
     */
    List<AIChatMessage> findByChatSessionAndMessageTypeOrderByCreatedAtAsc(AIChatSession chatSession, AIChatMessage.MessageType messageType);

    /**
     * 统计会话的消息数量
     */
    long countByChatSession(AIChatSession chatSession);

    /**
     * 查找最近的消息
     */
    @Query("SELECT m FROM AIChatMessage m WHERE m.chatSession = :session " +
           "ORDER BY m.createdAt DESC")
    List<AIChatMessage> findRecentMessages(@Param("session") AIChatSession session, Pageable pageable);

    /**
     * 查找有用的AI回复
     */
    @Query("SELECT m FROM AIChatMessage m WHERE m.chatSession = :session " +
           "AND m.messageType = 'AI' AND m.isHelpful = true " +
           "ORDER BY m.createdAt DESC")
    List<AIChatMessage> findHelpfulAIReplies(@Param("session") AIChatSession session);
}