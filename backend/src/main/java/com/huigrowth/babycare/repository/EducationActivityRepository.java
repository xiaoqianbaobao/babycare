package com.huigrowth.babycare.repository;

import com.huigrowth.babycare.entity.EducationActivity;
import com.huigrowth.babycare.entity.EducationPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教育活动数据访问层
 * 
 * @author HuiGrowth Team
 */
@Repository
public interface EducationActivityRepository extends JpaRepository<EducationActivity, Long> {

    /**
     * 查找计划的活动
     */
    Page<EducationActivity> findByEducationPlanOrderByScheduledTimeAsc(EducationPlan educationPlan, Pageable pageable);

    /**
     * 按状态查找计划的活动
     */
    List<EducationActivity> findByEducationPlanAndStatusOrderByScheduledTimeAsc(EducationPlan educationPlan, EducationActivity.ActivityStatus status);

    /**
     * 按类型查找计划的活动
     */
    List<EducationActivity> findByEducationPlanAndTypeOrderByScheduledTimeAsc(EducationPlan educationPlan, EducationActivity.ActivityType type);

    /**
     * 查找今日活动
     */
    @Query("SELECT ea FROM EducationActivity ea WHERE ea.educationPlan = :plan " +
           "AND DATE(ea.scheduledTime) = DATE(:date) " +
           "ORDER BY ea.scheduledTime ASC")
    List<EducationActivity> findTodayActivities(@Param("plan") EducationPlan plan, @Param("date") LocalDateTime date);

    /**
     * 查找即将开始的活动
     */
    @Query("SELECT ea FROM EducationActivity ea WHERE ea.educationPlan IN :plans " +
           "AND ea.status = 'PENDING' " +
           "AND ea.scheduledTime BETWEEN :startTime AND :endTime " +
           "ORDER BY ea.scheduledTime ASC")
    List<EducationActivity> findUpcomingActivities(@Param("plans") List<EducationPlan> plans,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 统计计划的活动数量（按状态）
     */
    long countByEducationPlanAndStatus(EducationPlan educationPlan, EducationActivity.ActivityStatus status);

    /**
     * 查找过期未完成的活动
     */
    @Query("SELECT ea FROM EducationActivity ea WHERE ea.educationPlan = :plan " +
           "AND ea.status = 'PENDING' " +
           "AND ea.scheduledTime < :currentTime " +
           "ORDER BY ea.scheduledTime DESC")
    List<EducationActivity> findOverdueActivities(@Param("plan") EducationPlan plan, @Param("currentTime") LocalDateTime currentTime);
}