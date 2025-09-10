package com.huigrowth.babycare.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huigrowth.babycare.dto.FamilyTaskCreateRequest;
import com.huigrowth.babycare.dto.FamilyTaskResponse;
import com.huigrowth.babycare.entity.Family;
import com.huigrowth.babycare.entity.FamilyTask;
import com.huigrowth.babycare.entity.User;
import com.huigrowth.babycare.exception.BusinessException;
import com.huigrowth.babycare.repository.FamilyTaskRepository;
import com.huigrowth.babycare.repository.FamilyRepository;
import com.huigrowth.babycare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 家庭任务服务
 * 
 * @author HuiGrowth Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyTaskService {

    private final FamilyTaskRepository familyTaskRepository;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建家庭任务
     */
    @Transactional
    public FamilyTaskResponse createTask(String username, FamilyTaskCreateRequest request) {
        log.info("创建家庭任务: username={}, request={}", username, request);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找家庭
        Family family = familyRepository.findById(request.getFamilyId())
                .orElseThrow(() -> new BusinessException("家庭不存在"));

        // 验证用户是否有权限访问该家庭
        if (!hasAccessToFamily(user, family)) {
            throw new BusinessException("您没有权限在该家庭创建任务");
        }

        // 查找被分配的用户
        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new BusinessException("被分配的用户不存在"));

        // 验证被分配的用户是否属于该家庭
        if (!hasAccessToFamily(assignee, family)) {
            throw new BusinessException("被分配的用户不属于该家庭");
        }

        // 创建家庭任务
        FamilyTask task = new FamilyTask();
        task.setFamily(family);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssignedTo("[" + assignee.getId() + "]"); // 简化处理，实际可能支持多个分配
        task.setAssignedBy(user);
        task.setDueDate(request.getDueDate());
        task.setPriority(FamilyTask.TaskPriority.valueOf(request.getPriority()));
        task.setCategory(FamilyTask.TaskCategory.valueOf(request.getCategory()));
        task.setStatus(FamilyTask.TaskStatus.PENDING);
        task.setReminderTime(request.getReminderTime());
        task.setIsRecurring(false);

        FamilyTask savedTask = familyTaskRepository.save(task);
        log.info("成功创建家庭任务: id={}", savedTask.getId());

        return convertToTaskResponse(savedTask);
    }

    /**
     * 获取家庭的任务
     */
    public Page<FamilyTaskResponse> getFamilyTasks(String username, Long familyId, int page, int size) {
        log.info("获取家庭任务: username={}, familyId={}, page={}, size={}", username, familyId, page, size);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找家庭
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BusinessException("家庭不存在"));

        // 验证用户是否有权限访问该家庭
        if (!hasAccessToFamily(user, family)) {
            throw new BusinessException("您没有权限查看该家庭的任务");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<FamilyTask> tasks = familyTaskRepository.findByFamilyOrderByCreatedAtDesc(family, pageable);

        return tasks.map(this::convertToTaskResponse);
    }

    /**
     * 获取我的任务
     */
    public Page<FamilyTaskResponse> getMyTasks(String username, int page, int size) {
        log.info("获取我的任务: username={}, page={}, size={}", username, page, size);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 获取用户所属的所有家庭
        List<Family> families = user.getFamilyMembers().stream()
                .filter(member -> member.getActive())
                .map(member -> member.getFamily())
                .collect(Collectors.toList());

        if (families.isEmpty()) {
            Pageable pageable = PageRequest.of(page, size);
            return Page.empty(pageable);
        }

        Pageable pageable = PageRequest.of(page, size);
        // 使用正确的repository方法查找分配给当前用户的任务
        Page<FamilyTask> tasks = familyTaskRepository.findByFamilyInAndAssignedToContainingOrderByCreatedAtDesc(
                families, user.getId(), pageable);

        return tasks.map(this::convertToTaskResponse);
    }

    /**
     * 开始任务
     */
    @Transactional
    public FamilyTaskResponse startTask(String username, Long taskId) {
        log.info("开始任务: username={}, taskId={}", username, taskId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找任务
        FamilyTask task = familyTaskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        // 验证权限
        if (!hasAccessToFamily(user, task.getFamily())) {
            throw new BusinessException("您没有权限操作该任务");
        }

        // 更新任务状态
        task.setStatus(FamilyTask.TaskStatus.IN_PROGRESS);
        FamilyTask savedTask = familyTaskRepository.save(task);

        return convertToTaskResponse(savedTask);
    }

    /**
     * 完成任务
     */
    @Transactional
    public FamilyTaskResponse completeTask(String username, Long taskId, String completionNotes) {
        log.info("完成任务: username={}, taskId={}", username, taskId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找任务
        FamilyTask task = familyTaskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        // 验证权限
        if (!hasAccessToFamily(user, task.getFamily())) {
            throw new BusinessException("您没有权限操作该任务");
        }

        // 更新任务状态
        task.setStatus(FamilyTask.TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setCompletedBy(user);
        task.setCompletionNotes(completionNotes);
        FamilyTask savedTask = familyTaskRepository.save(task);

        return convertToTaskResponse(savedTask);
    }

    /**
     * 取消任务
     */
    @Transactional
    public FamilyTaskResponse cancelTask(String username, Long taskId) {
        log.info("取消任务: username={}, taskId={}", username, taskId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找任务
        FamilyTask task = familyTaskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        // 验证权限
        if (!hasAccessToFamily(user, task.getFamily())) {
            throw new BusinessException("您没有权限操作该任务");
        }

        // 更新任务状态
        task.setStatus(FamilyTask.TaskStatus.CANCELLED);
        FamilyTask savedTask = familyTaskRepository.save(task);

        return convertToTaskResponse(savedTask);
    }

    /**
     * 删除任务
     */
    @Transactional
    public void deleteTask(String username, Long taskId) {
        log.info("删除任务: username={}, taskId={}", username, taskId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找任务
        FamilyTask task = familyTaskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        // 验证权限（只有分配者或家庭创建者可以删除）
        if (!task.getAssignedBy().getId().equals(user.getId()) && 
            !isFamilyCreator(user, task.getFamily())) {
            throw new BusinessException("您没有权限删除该任务");
        }

        familyTaskRepository.delete(task);
        log.info("成功删除任务: id={}", taskId);
    }

    /**
     * 检查用户是否有权限访问家庭
     */
    private boolean hasAccessToFamily(User user, Family family) {
        return family.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()) && member.getActive());
    }

    /**
     * 检查用户是否为家庭创建者
     */
    private boolean isFamilyCreator(User user, Family family) {
        return family.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()) && 
                                   member.getRole() == com.huigrowth.babycare.entity.FamilyMember.FamilyRole.CREATOR);
    }

    /**
     * 转换为任务响应DTO
     */
    private FamilyTaskResponse convertToTaskResponse(FamilyTask task) {
        FamilyTaskResponse response = new FamilyTaskResponse();
        response.setId(task.getId());
        response.setFamilyId(task.getFamily().getId());
        response.setFamilyName(task.getFamily().getName());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        
        // 设置被分配者信息
        if (task.getAssignedBy() != null) {
            response.setAssigneeId(task.getAssignedBy().getId());
            response.setAssigneeUsername(task.getAssignedBy().getUsername());
            response.setAssigneeNickname(task.getAssignedBy().getNickname());
        }
        
        // 设置分配者信息
        if (task.getAssignedBy() != null) {
            response.setAssignedById(task.getAssignedBy().getId());
            response.setAssignedByUsername(task.getAssignedBy().getUsername());
            response.setAssignedByNickname(task.getAssignedBy().getNickname());
        }
        
        response.setDueDate(task.getDueDate());
        response.setPriority(task.getPriority());
        response.setStatus(task.getStatus());
        response.setCategory(task.getCategory());
        response.setCompletedAt(task.getCompletedAt());
        
        // 设置完成者信息
        if (task.getCompletedBy() != null) {
            response.setCompletedById(task.getCompletedBy().getId());
            response.setCompletedByUsername(task.getCompletedBy().getUsername());
            response.setCompletedByNickname(task.getCompletedBy().getNickname());
        }
        
        response.setCompletionNotes(task.getCompletionNotes());
        response.setReminderTime(task.getReminderTime());
        response.setIsRecurring(task.getIsRecurring());
        response.setRecurrencePattern(task.getRecurrencePattern());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        return response;
    }
}