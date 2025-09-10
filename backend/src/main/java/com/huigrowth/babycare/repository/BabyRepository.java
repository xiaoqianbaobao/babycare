package com.huigrowth.babycare.repository;

import com.huigrowth.babycare.entity.Baby;
import com.huigrowth.babycare.entity.Family;
import com.huigrowth.babycare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 宝宝数据访问层
 * 
 * @author HuiGrowth Team
 */
@Repository
public interface BabyRepository extends JpaRepository<Baby, Long> {

    /**
     * 查找家庭的所有宝宝
     */
    List<Baby> findByFamilyOrderByBirthdayDesc(Family family);

    /**
     * 查找家庭的所有宝宝（简单版本）
     */
    List<Baby> findByFamily(Family family);

    /**
     * 查找用户可访问的宝宝
     */
    @Query("SELECT b FROM Baby b " +
           "JOIN b.family f " +
           "JOIN f.members fm " +
           "WHERE fm.user = :user AND fm.active = true " +
           "ORDER BY b.birthday DESC")
    List<Baby> findByUser(@Param("user") User user);

    /**
     * 按生日范围查找宝宝
     */
    List<Baby> findByBirthdayBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 查找特定年龄范围的宝宝
     */
    @Query("SELECT b FROM Baby b WHERE " +
           "FUNCTION('DATEDIFF', CURRENT_DATE, b.birthday) / 30 BETWEEN :minAgeMonths AND :maxAgeMonths")
    List<Baby> findByAgeInMonthsBetween(@Param("minAgeMonths") int minAgeMonths, 
                                       @Param("maxAgeMonths") int maxAgeMonths);

    /**
     * 按性别查找宝宝
     */
    List<Baby> findByGender(Baby.Gender gender);

    /**
     * 统计家庭中的宝宝数量
     */
    long countByFamily(Family family);
}