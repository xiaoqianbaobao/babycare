package com.huigrowth.babycare.repository;

import com.huigrowth.babycare.entity.Family;
import com.huigrowth.babycare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 家庭数据访问层
 * 
 * @author HuiGrowth Team
 */
@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {

    /**
     * 通过邀请码查找家庭
     */
    Optional<Family> findByInviteCode(String inviteCode);

    /**
     * 检查邀请码是否存在
     */
    boolean existsByInviteCode(String inviteCode);

    /**
     * 查找用户所属的家庭
     */
    @Query("SELECT DISTINCT f FROM Family f " +
           "JOIN f.members fm " +
           "WHERE fm.user = :user AND fm.active = true")
    List<Family> findByUser(@Param("user") User user);

    /**
     * 查找用户创建的家庭
     */
    @Query("SELECT f FROM Family f " +
           "JOIN f.members fm " +
           "WHERE fm.user = :user AND fm.role = 'CREATOR'")
    List<Family> findByCreator(@Param("user") User user);

    /**
     * 通过家庭名称模糊查询
     */
    @Query("SELECT f FROM Family f WHERE f.name LIKE %:name%")
    List<Family> findByNameContaining(@Param("name") String name);
}