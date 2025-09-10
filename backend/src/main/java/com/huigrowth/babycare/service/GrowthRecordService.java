package com.huigrowth.babycare.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huigrowth.babycare.dto.GrowthRecordCreateRequest;
import com.huigrowth.babycare.dto.GrowthRecordResponse;
import com.huigrowth.babycare.entity.Baby;
import com.huigrowth.babycare.entity.GrowthRecord;
import com.huigrowth.babycare.entity.User;
import com.huigrowth.babycare.exception.BusinessException;
import com.huigrowth.babycare.repository.BabyRepository;
import com.huigrowth.babycare.repository.FamilyMemberRepository;
import com.huigrowth.babycare.repository.GrowthRecordRepository;
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
 * 成长记录服务
 * 
 * @author HuiGrowth Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GrowthRecordService {

    private final GrowthRecordRepository growthRecordRepository;
    private final BabyRepository babyRepository;
    private final UserRepository userRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final ObjectMapper objectMapper;

    /**
     * 创建成长记录
     */
    @Transactional
    public GrowthRecordResponse createRecord(String username, GrowthRecordCreateRequest request) {
        log.info("创建成长记录: username={}, request={}", username, request);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找宝宝
        Baby baby = babyRepository.findById(request.getBabyId())
                .orElseThrow(() -> new BusinessException("宝宝不存在"));

        // 验证用户是否有权限访问该宝宝
        if (!hasAccessToBaby(user, baby)) {
            throw new BusinessException("您没有权限为该宝宝创建记录");
        }

        // 创建成长记录
        GrowthRecord record = new GrowthRecord();
        record.setBaby(baby);
        record.setType(GrowthRecord.RecordType.valueOf(request.getType()));
        record.setTitle(request.getTitle());
        record.setContent(request.getContent());
        record.setCreatedBy(user);
        
        // 处理媒体URLs
        if (request.getMediaUrls() != null && !request.getMediaUrls().isEmpty()) {
            try {
                record.setMediaUrls(objectMapper.writeValueAsString(request.getMediaUrls()));
            } catch (JsonProcessingException e) {
                log.error("序列化媒体URLs失败", e);
                throw new BusinessException("媒体URLs格式错误");
            }
        }

        // 处理标签
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            try {
                record.setTags(objectMapper.writeValueAsString(request.getTags()));
            } catch (JsonProcessingException e) {
                log.error("序列化标签失败", e);
                throw new BusinessException("标签格式错误");
            }
        }

        GrowthRecord savedRecord = growthRecordRepository.save(record);
        log.info("成功创建成长记录: id={}", savedRecord.getId());

        return convertToResponse(savedRecord);
    }

    /**
     * 获取宝宝的成长记录（分页）
     */
    public Page<GrowthRecordResponse> getBabyRecords(String username, Long babyId, int page, int size) {
        log.info("获取宝宝成长记录: username={}, babyId={}, page={}, size={}", username, babyId, page, size);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找宝宝
        Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new BusinessException("宝宝不存在"));

        // 验证用户是否有权限访问该宝宝
        if (!hasAccessToBaby(user, baby)) {
            throw new BusinessException("您没有权限查看该宝宝的记录");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<GrowthRecord> records = growthRecordRepository.findByBabyOrderByCreatedAtDesc(baby, pageable);

        return records.map(this::convertToResponse);
    }

    /**
     * 按类型获取成长记录
     */
    public List<GrowthRecordResponse> getRecordsByType(String username, Long babyId, String type) {
        log.info("按类型获取成长记录: username={}, babyId={}, type={}", username, babyId, type);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找宝宝
        Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new BusinessException("宝宝不存在"));

        // 验证用户是否有权限访问该宝宝
        if (!hasAccessToBaby(user, baby)) {
            throw new BusinessException("您没有权限查看该宝宝的记录");
        }

        GrowthRecord.RecordType recordType;
        try {
            recordType = GrowthRecord.RecordType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("不支持的记录类型: " + type);
        }

        List<GrowthRecord> records = growthRecordRepository.findByBabyAndTypeOrderByCreatedAtDesc(baby, recordType);
        return records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 搜索成长记录
     */
    public List<GrowthRecordResponse> searchRecords(String username, Long babyId, String keyword) {
        log.info("搜索成长记录: username={}, babyId={}, keyword={}", username, babyId, keyword);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找宝宝
        Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new BusinessException("宝宝不存在"));

        // 验证用户是否有权限访问该宝宝
        if (!hasAccessToBaby(user, baby)) {
            throw new BusinessException("您没有权限查看该宝宝的记录");
        }

        List<GrowthRecord> records = growthRecordRepository.searchByKeyword(baby, keyword);
        return records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取最近的成长记录
     */
    public List<GrowthRecordResponse> getRecentRecords(String username, int limit) {
        log.info("获取最近的成长记录: username={}, limit={}", username, limit);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 获取用户可访问的宝宝
        List<Baby> babies = babyRepository.findByUser(user);
        if (babies.isEmpty()) {
            return new ArrayList<>();
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<GrowthRecord> records = growthRecordRepository.findRecentRecords(babies, pageable);

        return records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 增加查看次数
     */
    @Transactional
    public void incrementViewCount(Long recordId) {
        GrowthRecord record = growthRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException("记录不存在"));
        
        record.setViewCount(record.getViewCount() + 1);
        growthRecordRepository.save(record);
    }

    /**
     * 检查用户是否有权限访问宝宝
     */
    private boolean hasAccessToBaby(User user, Baby baby) {
        // 通过家庭成员关系检查权限
        return familyMemberRepository.existsByUserAndBaby(user, baby.getFamily().getId());
    }

    /**
     * 转换为响应DTO
     */
    private GrowthRecordResponse convertToResponse(GrowthRecord record) {
        GrowthRecordResponse response = new GrowthRecordResponse();
        response.setId(record.getId());
        response.setBabyId(record.getBaby().getId());
        response.setBabyName(record.getBaby().getName());
        response.setType(record.getType());
        response.setTitle(record.getTitle());
        response.setContent(record.getContent());
        response.setLocation(record.getLocation());
        response.setWeather(record.getWeather());
        response.setMood(record.getMood());
        response.setViewCount(record.getViewCount());
        response.setLikeCount(record.getLikeCount());
        response.setCreatedBy(record.getCreatedBy().getUsername());
        response.setCreatedByNickname(record.getCreatedBy().getNickname());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());

        // 处理媒体URLs
        if (record.getMediaUrls() != null && !record.getMediaUrls().isEmpty()) {
            try {
                List<String> mediaUrls = objectMapper.readValue(record.getMediaUrls(), new TypeReference<List<String>>() {});
                response.setMediaUrls(mediaUrls);
            } catch (JsonProcessingException e) {
                log.error("反序列化媒体URLs失败: recordId={}", record.getId(), e);
                response.setMediaUrls(new ArrayList<>());
            }
        } else {
            response.setMediaUrls(new ArrayList<>());
        }

        // 处理标签
        if (record.getTags() != null && !record.getTags().isEmpty()) {
            try {
                List<String> tags = objectMapper.readValue(record.getTags(), new TypeReference<List<String>>() {});
                response.setTags(tags);
            } catch (JsonProcessingException e) {
                log.error("反序列化标签失败: recordId={}", record.getId(), e);
                response.setTags(new ArrayList<>());
            }
        } else {
            response.setTags(new ArrayList<>());
        }

        return response;
    }
}