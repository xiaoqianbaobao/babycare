package com.huigrowth.babycare.service;

import com.huigrowth.babycare.dto.*;
import com.huigrowth.babycare.entity.*;
import com.huigrowth.babycare.exception.BusinessException;
import com.huigrowth.babycare.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教育计划服务
 * 
 * @author HuiGrowth Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EducationPlanService {

    private final EducationPlanRepository educationPlanRepository;
    private final EducationActivityRepository educationActivityRepository;
    private final BabyRepository babyRepository;
    private final UserRepository userRepository;

    /**
     * 创建教育计划
     */
    @Transactional
    public EducationPlanResponse createPlan(String username, EducationPlanCreateRequest request) {
        log.info("创建教育计划: username={}, request={}", username, request);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找宝宝
        Baby baby = babyRepository.findById(request.getBabyId())
                .orElseThrow(() -> new BusinessException("宝宝不存在"));

        // 验证用户是否有权限访问该宝宝
        if (!hasAccessToBaby(user, baby)) {
            throw new BusinessException("您没有权限为该宝宝创建教育计划");
        }

        // 创建教育计划
        EducationPlan plan = new EducationPlan();
        plan.setBaby(baby);
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setCategory(EducationPlan.EducationCategory.valueOf(request.getCategory()));
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setTargetAgeMonths(request.getTargetAgeMonths());
        plan.setDifficultyLevel(request.getDifficultyLevel());
        plan.setGoals(request.getGoals());
        plan.setCreatedBy(user);

        EducationPlan savedPlan = educationPlanRepository.save(plan);
        log.info("成功创建教育计划: id={}", savedPlan.getId());

        return convertToPlanResponse(savedPlan);
    }

    /**
     * 更新教育计划
     */
    @Transactional
    public EducationPlanResponse updatePlan(String username, Long planId, EducationPlanCreateRequest request) {
        log.info("更新教育计划: username={}, planId={}, request={}", username, planId, request);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找计划
        EducationPlan plan = educationPlanRepository.findById(planId)
                .orElseThrow(() -> new BusinessException("教育计划不存在"));

        // 验证权限
        if (!hasAccessToBaby(user, plan.getBaby())) {
            throw new BusinessException("您没有权限操作该教育计划");
        }

        // 更新教育计划
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setCategory(EducationPlan.EducationCategory.valueOf(request.getCategory()));
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setTargetAgeMonths(request.getTargetAgeMonths());
        plan.setDifficultyLevel(request.getDifficultyLevel());
        plan.setGoals(request.getGoals());

        EducationPlan savedPlan = educationPlanRepository.save(plan);
        log.info("成功更新教育计划: id={}", savedPlan.getId());

        return convertToPlanResponse(savedPlan);
    }

    /**
     * 删除教育计划
     */
    @Transactional
    public void deletePlan(String username, Long planId) {
        log.info("删除教育计划: username={}, planId={}", username, planId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找计划
        EducationPlan plan = educationPlanRepository.findById(planId)
                .orElseThrow(() -> new BusinessException("教育计划不存在"));

        // 验证权限
        if (!hasAccessToBaby(user, plan.getBaby())) {
            throw new BusinessException("您没有权限操作该教育计划");
        }

        // 删除相关的活动
        educationActivityRepository.deleteByEducationPlan(plan);
        
        // 删除计划
        educationPlanRepository.delete(plan);
        log.info("成功删除教育计划: id={}", planId);
    }

    /**
     * 获取宝宝的教育计划（分页）
     */
    public Page<EducationPlanResponse> getBabyPlans(String username, Long babyId, int page, int size) {
        log.info("获取宝宝教育计划: username={}, babyId={}, page={}, size={}", username, babyId, page, size);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找宝宝
        Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new BusinessException("宝宝不存在"));

        // 验证用户是否有权限访问该宝宝
        if (!hasAccessToBaby(user, baby)) {
            throw new BusinessException("您没有权限查看该宝宝的教育计划");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<EducationPlan> plans = educationPlanRepository.findByBabyOrderByCreatedAtDesc(baby, pageable);

        return plans.map(this::convertToPlanResponse);
    }

    /**
     * 获取进行中的计划
     */
    public List<EducationPlanResponse> getActivePlans(String username, Long babyId) {
        log.info("获取进行中的教育计划: username={}, babyId={}", username, babyId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找宝宝
        Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new BusinessException("宝宝不存在"));

        // 验证用户是否有权限访问该宝宝
        if (!hasAccessToBaby(user, baby)) {
            throw new BusinessException("您没有权限查看该宝宝的教育计划");
        }

        List<EducationPlan> plans = educationPlanRepository.findActivePlans(baby, LocalDate.now());
        return plans.stream()
                .map(this::convertToPlanResponse)
                .collect(Collectors.toList());
    }

    /**
     * 启动计划
     */
    @Transactional
    public EducationPlanResponse startPlan(String username, Long planId) {
        log.info("启动教育计划: username={}, planId={}", username, planId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找计划
        EducationPlan plan = educationPlanRepository.findById(planId)
                .orElseThrow(() -> new BusinessException("教育计划不存在"));

        // 验证权限
        if (!hasAccessToBaby(user, plan.getBaby())) {
            throw new BusinessException("您没有权限操作该教育计划");
        }

        // 更新状态
        plan.setStatus(EducationPlan.PlanStatus.ACTIVE);
        EducationPlan savedPlan = educationPlanRepository.save(plan);

        return convertToPlanResponse(savedPlan);
    }

    /**
     * 完成计划
     */
    @Transactional
    public EducationPlanResponse completePlan(String username, Long planId) {
        log.info("完成教育计划: username={}, planId={}", username, planId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找计划
        EducationPlan plan = educationPlanRepository.findById(planId)
                .orElseThrow(() -> new BusinessException("教育计划不存在"));

        // 验证权限
        if (!hasAccessToBaby(user, plan.getBaby())) {
            throw new BusinessException("您没有权限操作该教育计划");
        }

        // 更新状态和进度
        plan.setStatus(EducationPlan.PlanStatus.COMPLETED);
        plan.setProgressPercentage(100);
        EducationPlan savedPlan = educationPlanRepository.save(plan);

        return convertToPlanResponse(savedPlan);
    }

    /**
     * 创建教育活动
     */
    @Transactional
    public EducationActivityResponse createActivity(String username, EducationActivityCreateRequest request) {
        log.info("创建教育活动: username={}, request={}", username, request);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找教育计划
        EducationPlan plan = educationPlanRepository.findById(request.getEducationPlanId())
                .orElseThrow(() -> new BusinessException("教育计划不存在"));

        // 验证权限
        if (!hasAccessToBaby(user, plan.getBaby())) {
            throw new BusinessException("您没有权限为该计划创建活动");
        }

        // 创建教育活动
        EducationActivity activity = new EducationActivity();
        activity.setEducationPlan(plan);
        activity.setName(request.getName());
        activity.setDescription(request.getDescription());
        activity.setType(EducationActivity.ActivityType.valueOf(request.getType()));
        activity.setScheduledTime(request.getScheduledTime());
        activity.setDurationMinutes(request.getDurationMinutes());
        activity.setMaterials(request.getMaterials());
        activity.setInstructions(request.getInstructions());

        EducationActivity savedActivity = educationActivityRepository.save(activity);
        log.info("成功创建教育活动: id={}", savedActivity.getId());

        return convertToActivityResponse(savedActivity);
    }

    /**
     * 获取计划的活动
     */
    public Page<EducationActivityResponse> getPlanActivities(String username, Long planId, int page, int size) {
        log.info("获取计划活动: username={}, planId={}, page={}, size={}", username, planId, page, size);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找教育计划
        EducationPlan plan = educationPlanRepository.findById(planId)
                .orElseThrow(() -> new BusinessException("教育计划不存在"));

        // 验证权限
        if (!hasAccessToBaby(user, plan.getBaby())) {
            throw new BusinessException("您没有权限查看该计划的活动");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<EducationActivity> activities = educationActivityRepository.findByEducationPlanOrderByScheduledTimeAsc(plan, pageable);

        return activities.map(this::convertToActivityResponse);
    }

    /**
     * 完成活动
     */
    @Transactional
    public EducationActivityResponse completeActivity(String username, Long activityId, String notes, Integer rating) {
        log.info("完成教育活动: username={}, activityId={}, rating={}", username, activityId, rating);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找活动
        EducationActivity activity = educationActivityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException("教育活动不存在"));

        // 验证权限
        if (!hasAccessToBaby(user, activity.getEducationPlan().getBaby())) {
            throw new BusinessException("您没有权限操作该活动");
        }

        // 更新活动状态
        activity.setStatus(EducationActivity.ActivityStatus.COMPLETED);
        activity.setActualEndTime(LocalDateTime.now());
        activity.setCompletionPercentage(100);
        activity.setNotes(notes);
        activity.setRating(rating);

        if (activity.getActualStartTime() == null) {
            activity.setActualStartTime(LocalDateTime.now());
        }

        EducationActivity savedActivity = educationActivityRepository.save(activity);

        // 更新计划进度
        updatePlanProgress(activity.getEducationPlan());

        return convertToActivityResponse(savedActivity);
    }

    /**
     * 检查用户是否有权限访问宝宝
     */
    private boolean hasAccessToBaby(User user, Baby baby) {
        return baby.getFamily().getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()) && member.getActive());
    }

    /**
     * 更新计划进度
     */
    private void updatePlanProgress(EducationPlan plan) {
        long totalActivities = educationActivityRepository.countByEducationPlanAndStatus(plan, null);
        long completedActivities = educationActivityRepository.countByEducationPlanAndStatus(plan, EducationActivity.ActivityStatus.COMPLETED);
        
        if (totalActivities > 0) {
            int progress = (int) ((completedActivities * 100) / totalActivities);
            plan.setProgressPercentage(progress);
            educationPlanRepository.save(plan);
        }
    }

    /**
     * 转换为计划响应DTO
     */
    private EducationPlanResponse convertToPlanResponse(EducationPlan plan) {
        EducationPlanResponse response = new EducationPlanResponse();
        response.setId(plan.getId());
        response.setBabyId(plan.getBaby().getId());
        response.setBabyName(plan.getBaby().getName());
        response.setName(plan.getName());
        response.setDescription(plan.getDescription());
        response.setCategory(plan.getCategory());
        response.setStatus(plan.getStatus());
        response.setStartDate(plan.getStartDate());
        response.setEndDate(plan.getEndDate());
        response.setTargetAgeMonths(plan.getTargetAgeMonths());
        response.setDifficultyLevel(plan.getDifficultyLevel());
        response.setGoals(plan.getGoals());
        response.setProgressPercentage(plan.getProgressPercentage());
        response.setCreatedBy(plan.getCreatedBy().getUsername());
        response.setCreatedByNickname(plan.getCreatedBy().getNickname());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());

        // 统计活动数量
        long totalActivities = educationActivityRepository.countByEducationPlan(plan);
        long completedActivities = educationActivityRepository.countByEducationPlanAndStatus(plan, EducationActivity.ActivityStatus.COMPLETED);
        long pendingActivities = educationActivityRepository.countByEducationPlanAndStatus(plan, EducationActivity.ActivityStatus.PENDING);
        
        response.setTotalActivities((int) totalActivities);
        response.setCompletedActivities((int) completedActivities);
        response.setPendingActivities((int) pendingActivities);

        return response;
    }

    /**
     * 转换为活动响应DTO
     */
    private EducationActivityResponse convertToActivityResponse(EducationActivity activity) {
        EducationActivityResponse response = new EducationActivityResponse();
        response.setId(activity.getId());
        response.setEducationPlanId(activity.getEducationPlan().getId());
        response.setEducationPlanName(activity.getEducationPlan().getName());
        response.setName(activity.getName());
        response.setDescription(activity.getDescription());
        response.setType(activity.getType());
        response.setStatus(activity.getStatus());
        response.setScheduledTime(activity.getScheduledTime());
        response.setActualStartTime(activity.getActualStartTime());
        response.setActualEndTime(activity.getActualEndTime());
        response.setDurationMinutes(activity.getDurationMinutes());
        response.setMaterials(activity.getMaterials());
        response.setInstructions(activity.getInstructions());
        response.setNotes(activity.getNotes());
        response.setRating(activity.getRating());
        response.setCompletionPercentage(activity.getCompletionPercentage());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());

        return response;
    }
}