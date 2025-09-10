package com.huigrowth.babycare.repository;

import com.huigrowth.babycare.entity.FamilyPost;
import com.huigrowth.babycare.entity.Family;
import com.huigrowth.babycare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 家庭动态数据访问层
 * 
 * @author HuiGrowth Team
 */
@Repository
public interface FamilyPostRepository extends JpaRepository<FamilyPost, Long> {

    /**
     * 查找家庭的动态
     */
    Page<FamilyPost> findByFamilyOrderByCreatedAtDesc(Family family, Pageable pageable);

    /**
     * 查找用户发布的动态
     */
    Page<FamilyPost> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    /**
     * 按类型查找家庭的动态
     */
    Page<FamilyPost> findByFamilyAndPostTypeOrderByCreatedAtDesc(Family family, FamilyPost.PostType postType, Pageable pageable);

    /**
     * 查找置顶的动态
     */
    List<FamilyPost> findByFamilyAndIsPinnedTrueOrderByCreatedAtDesc(Family family);

    /**
     * 查找最近的动态
     */
    @Query("SELECT fp FROM FamilyPost fp WHERE fp.family IN :families ORDER BY fp.createdAt DESC")
    Page<FamilyPost> findRecentPostsByFamilies(@Param("families") List<Family> families, Pageable pageable);

    /**
     * 统计家庭的动态数量
     */
    long countByFamily(Family family);

    /**
     * 统计用户的动态数量
     */
    long countByAuthor(User author);
}