package com.huigrowth.babycare.repository;

import com.huigrowth.babycare.entity.FamilyTask;
import com.huigrowth.babycare.entity.Family;
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
 * 家庭任务数据访问层
 * 
 * @author HuiGrowth Team
 */
@Repository
public interface FamilyTaskRepository extends JpaRepository<FamilyTask, Long> {

    /**
     * 查找家庭的任务
     */
    Page<FamilyTask> findByFamilyOrderByCreatedAtDesc(Family family, Pageable pageable);

    /**
     * 按状态查找家庭的任务
     */
    Page<FamilyTask> findByFamilyAndStatusOrderByCreatedAtDesc(Family family, FamilyTask.TaskStatus status, Pageable pageable);

    /**
     * 按优先级查找家庭的任务
     */
    Page<FamilyTask> findByFamilyAndPriorityOrderByCreatedAtDesc(Family family, FamilyTask.TaskPriority priority, Pageable pageable);

    /**
     * 按类别查找家庭的任务
     */
    Page<FamilyTask> findByFamilyAndCategoryOrderByCreatedAtDesc(Family family, FamilyTask.TaskCategory category, Pageable pageable);

    /**
     * 查找分配给用户的任务
     */
    @Query("SELECT ft FROM FamilyTask ft WHERE ft.family IN :families " +
           "AND ft.assignedTo LIKE CONCAT('%', :userId, '%') " +
           "ORDER BY ft.createdAt DESC")
    Page<FamilyTask> findByFamilyInAndAssignedToContainingOrderByCreatedAtDesc(
            @Param("families") List<Family> families, 
            @Param("userId") Long userId, 
            Pageable pageable);

    /**
     * 查找用户创建的任务
     */
    Page<FamilyTask> findByFamilyInAndAssignedByOrderByCreatedAtDesc(List<Family> families, User assignedBy, Pageable pageable);

    /**
     * 查找即将到期的任务
     */
    @Query("SELECT ft FROM FamilyTask ft WHERE ft.family IN :families " +
           "AND ft.status IN ('PENDING', 'IN_PROGRESS') " +
           "AND ft.dueDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ft.dueDate ASC")
    Page<FamilyTask> findUpcomingTasks(@Param("families") List<Family> families,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     Pageable pageable);

    /**
     * 查找过期未完成的任务
     */
    @Query("SELECT ft FROM FamilyTask ft WHERE ft.family IN :families " +
           "AND ft.status IN ('PENDING', 'IN_PROGRESS') " +
           "AND ft.dueDate < :currentTime " +
           "ORDER BY ft.dueDate ASC")
    Page<FamilyTask> findOverdueTasks(@Param("families") List<Family> families,
                                    @Param("currentTime") LocalDateTime currentTime,
                                    Pageable pageable);

    /**
     * 统计家庭的任务数量（按状态）
     */
    long countByFamilyAndStatus(Family family, FamilyTask.TaskStatus status);

    /**
     * 统计分配给用户的任务数量（按状态）
     */
    @Query("SELECT COUNT(ft) FROM FamilyTask ft WHERE ft.family IN :families " +
           "AND ft.assignedTo LIKE CONCAT('%', :userId, '%') " +
           "AND ft.status = :status")
    long countByFamilyInAndAssignedToContainingAndStatus(
            @Param("families") List<Family> families, 
            @Param("userId") Long userId, 
            @Param("status") FamilyTask.TaskStatus status);
}