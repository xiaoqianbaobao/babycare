package com.huigrowth.babycare.repository;

import com.huigrowth.babycare.entity.EducationPlan;
import com.huigrowth.babycare.entity.Baby;
import com.huigrowth.babycare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 教育计划数据访问层
 * 
 * @author HuiGrowth Team
 */
@Repository
public interface EducationPlanRepository extends JpaRepository<EducationPlan, Long> {

    /**
     * 查找宝宝的教育计划
     */
    Page<EducationPlan> findByBabyOrderByCreatedAtDesc(Baby baby, Pageable pageable);

    /**
     * 按状态查找宝宝的教育计划
     */
    List<EducationPlan> findByBabyAndStatusOrderByCreatedAtDesc(Baby baby, EducationPlan.PlanStatus status);

    /**
     * 按类别查找宝宝的教育计划
     */
    List<EducationPlan> findByBabyAndCategoryOrderByCreatedAtDesc(Baby baby, EducationPlan.EducationCategory category);

    /**
     * 查找用户创建的教育计划
     */
    Page<EducationPlan> findByCreatedByOrderByCreatedAtDesc(User createdBy, Pageable pageable);

    /**
     * 查找进行中的教育计划
     */
    @Query("SELECT ep FROM EducationPlan ep WHERE ep.baby = :baby " +
           "AND ep.status = 'ACTIVE' " +
           "AND ep.startDate <= :currentDate " +
           "AND (ep.endDate IS NULL OR ep.endDate >= :currentDate) " +
           "ORDER BY ep.startDate ASC")
    List<EducationPlan> findActivePlans(@Param("baby") Baby baby, @Param("currentDate") LocalDate currentDate);

    /**
     * 按日期范围查找教育计划
     */
    @Query("SELECT ep FROM EducationPlan ep WHERE ep.baby = :baby " +
           "AND ep.startDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ep.startDate DESC")
    List<EducationPlan> findByBabyAndDateRange(@Param("baby") Baby baby,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    /**
     * 统计宝宝的计划数量（按状态）
     */
    long countByBabyAndStatus(Baby baby, EducationPlan.PlanStatus status);

    /**
     * 查找即将到期的计划
     */
    @Query("SELECT ep FROM EducationPlan ep WHERE ep.baby IN :babies " +
           "AND ep.status = 'ACTIVE' " +
           "AND ep.endDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ep.endDate ASC")
    List<EducationPlan> findUpcomingDeadlines(@Param("babies") List<Baby> babies,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}