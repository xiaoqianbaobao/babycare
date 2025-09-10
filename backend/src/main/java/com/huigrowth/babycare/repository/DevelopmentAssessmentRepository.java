package com.huigrowth.babycare.repository;

import com.huigrowth.babycare.entity.DevelopmentAssessment;
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
 * 发育评估数据访问层
 * 
 * @author HuiGrowth Team
 */
@Repository
public interface DevelopmentAssessmentRepository extends JpaRepository<DevelopmentAssessment, Long> {

    /**
     * 查找宝宝的发育评估
     */
    Page<DevelopmentAssessment> findByBabyOrderByAssessmentDateDesc(Baby baby, Pageable pageable);

    /**
     * 按类别查找宝宝的评估
     */
    List<DevelopmentAssessment> findByBabyAndCategoryOrderByAssessmentDateDesc(Baby baby, DevelopmentAssessment.AssessmentCategory category);

    /**
     * 按状态查找宝宝的评估
     */
    List<DevelopmentAssessment> findByBabyAndStatusOrderByAssessmentDateDesc(Baby baby, DevelopmentAssessment.AssessmentStatus status);

    /**
     * 查找用户创建的评估
     */
    Page<DevelopmentAssessment> findByCreatedByOrderByAssessmentDateDesc(User createdBy, Pageable pageable);

    /**
     * 按日期范围查找评估
     */
    @Query("SELECT da FROM DevelopmentAssessment da WHERE da.baby = :baby " +
           "AND da.assessmentDate BETWEEN :startDate AND :endDate " +
           "ORDER BY da.assessmentDate DESC")
    List<DevelopmentAssessment> findByBabyAndDateRange(@Param("baby") Baby baby,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 查找最新的评估
     */
    @Query("SELECT da FROM DevelopmentAssessment da WHERE da.baby = :baby " +
           "AND da.category = :category " +
           "ORDER BY da.assessmentDate DESC")
    List<DevelopmentAssessment> findLatestAssessment(@Param("baby") Baby baby,
                                                   @Param("category") DevelopmentAssessment.AssessmentCategory category,
                                                   Pageable pageable);

    /**
     * 统计评估数量（按类别）
     */
    long countByBabyAndCategory(Baby baby, DevelopmentAssessment.AssessmentCategory category);

    /**
     * 查找需要下次评估的记录
     */
    @Query("SELECT da FROM DevelopmentAssessment da WHERE da.nextAssessmentDate <= :date " +
           "AND da.status = 'COMPLETED' " +
           "ORDER BY da.nextAssessmentDate ASC")
    List<DevelopmentAssessment> findDueForNextAssessment(@Param("date") LocalDate date);
}