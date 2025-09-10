package com.huigrowth.babycare.repository;

import com.huigrowth.babycare.entity.GrowthRecord;
import com.huigrowth.babycare.entity.Baby;
import com.huigrowth.babycare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 成长记录数据访问层
 * 
 * @author HuiGrowth Team
 */
@Repository
public interface GrowthRecordRepository extends JpaRepository<GrowthRecord, Long> {

    /**
     * 查找宝宝的成长记录（分页）
     */
    Page<GrowthRecord> findByBabyOrderByCreatedAtDesc(Baby baby, Pageable pageable);

    /**
     * 按类型查找宝宝的成长记录
     */
    List<GrowthRecord> findByBabyAndTypeOrderByCreatedAtDesc(Baby baby, GrowthRecord.RecordType type);

    /**
     * 查找用户创建的成长记录
     */
    Page<GrowthRecord> findByCreatedByOrderByCreatedAtDesc(User createdBy, Pageable pageable);

    /**
     * 按时间范围查找成长记录
     */
    @Query("SELECT gr FROM GrowthRecord gr WHERE gr.baby = :baby " +
           "AND gr.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY gr.createdAt DESC")
    List<GrowthRecord> findByBabyAndDateRange(@Param("baby") Baby baby,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    /**
     * 统计宝宝的记录数量（按类型）
     */
    long countByBabyAndType(Baby baby, GrowthRecord.RecordType type);

    /**
     * 搜索成长记录（标题和内容）
     */
    @Query("SELECT gr FROM GrowthRecord gr WHERE gr.baby = :baby " +
           "AND (gr.title LIKE %:keyword% OR gr.content LIKE %:keyword%) " +
           "ORDER BY gr.createdAt DESC")
    List<GrowthRecord> searchByKeyword(@Param("baby") Baby baby, @Param("keyword") String keyword);

    /**
     * 获取最近的成长记录
     */
    @Query("SELECT gr FROM GrowthRecord gr WHERE gr.baby IN :babies " +
           "ORDER BY gr.createdAt DESC")
    List<GrowthRecord> findRecentRecords(@Param("babies") List<Baby> babies, Pageable pageable);
}