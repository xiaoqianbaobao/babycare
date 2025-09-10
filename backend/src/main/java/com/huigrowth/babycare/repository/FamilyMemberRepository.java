package com.huigrowth.babycare.repository;

import com.huigrowth.babycare.entity.Family;
import com.huigrowth.babycare.entity.FamilyMember;
import com.huigrowth.babycare.entity.User;
import com.huigrowth.babycare.entity.Baby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 家庭成员数据访问层
 * 
 * @author HuiGrowth Team
 */
@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {

    /**
     * 通过用户查找家庭成员关系
     */
    List<FamilyMember> findByUser(User user);

    /**
     * 通过家庭查找成员列表
     */
    List<FamilyMember> findByFamily(Family family);

    /**
     * 检查用户是否是某个家庭的成员
     */
    boolean existsByUserAndFamily(User user, Family family);

    /**
     * 检查用户是否可以访问指定的宝宝
     */
    @Query("SELECT CASE WHEN COUNT(fm) > 0 THEN true ELSE false END FROM FamilyMember fm " +
           "WHERE fm.user = :user AND fm.family.id = :familyId AND fm.active = true")
    boolean existsByUserAndBaby(@Param("user") User user, @Param("familyId") Long familyId);

    /**
     * 检查用户是否有指定角色
     */
    boolean existsByUserAndRole(User user, FamilyMember.FamilyRole role);

    /**
     * 统计家庭成员数量
     */
    long countByFamily(Family family);

    /**
     * 通过用户和家庭查找成员关系
     */
    FamilyMember findByUserAndFamily(User user, Family family);
}