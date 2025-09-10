package com.huigrowth.babycare.repository;

import com.huigrowth.babycare.entity.AIChatSession;
import com.huigrowth.babycare.entity.User;
import com.huigrowth.babycare.entity.Baby;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI聊天会话数据访问层
 * 
 * @author HuiGrowth Team
 */
@Repository
public interface AIChatSessionRepository extends JpaRepository<AIChatSession, Long> {

    /**
     * 查找用户的聊天会话
     */
    Page<AIChatSession> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * 按状态查找用户的会话
     */
    List<AIChatSession> findByUserAndStatusOrderByCreatedAtDesc(User user, AIChatSession.SessionStatus status);

    /**
     * 按咨询类型查找用户的会话
     */
    List<AIChatSession> findByUserAndConsultationTypeOrderByCreatedAtDesc(User user, AIChatSession.ConsultationType consultationType);

    /**
     * 查找针对特定宝宝的会话
     */
    List<AIChatSession> findByBabyOrderByCreatedAtDesc(Baby baby);

    /**
     * 统计用户的会话数量（按状态）
     */
    long countByUserAndStatus(User user, AIChatSession.SessionStatus status);

    /**
     * 查找最近的活跃会话
     */
    @Query("SELECT s FROM AIChatSession s WHERE s.user = :user " +
           "AND s.status = 'ACTIVE' " +
           "ORDER BY s.updatedAt DESC")
    List<AIChatSession> findRecentActiveSessions(@Param("user") User user, Pageable pageable);
}