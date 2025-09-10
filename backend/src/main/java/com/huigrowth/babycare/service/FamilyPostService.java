package com.huigrowth.babycare.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huigrowth.babycare.dto.FamilyPostCreateRequest;
import com.huigrowth.babycare.dto.FamilyPostResponse;
import com.huigrowth.babycare.entity.Family;
import com.huigrowth.babycare.entity.FamilyPost;
import com.huigrowth.babycare.entity.User;
import com.huigrowth.babycare.exception.BusinessException;
import com.huigrowth.babycare.repository.FamilyPostRepository;
import com.huigrowth.babycare.repository.FamilyRepository;
import com.huigrowth.babycare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 家庭动态服务
 * 
 * @author HuiGrowth Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyPostService {

    private final FamilyPostRepository familyPostRepository;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建家庭动态
     */
    @Transactional
    public FamilyPostResponse createPost(String username, FamilyPostCreateRequest request) throws JsonProcessingException {
        log.info("创建家庭动态: username={}, request={}", username, request);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找家庭
        Family family = familyRepository.findById(request.getFamilyId())
                .orElseThrow(() -> new BusinessException("家庭不存在"));

        // 验证用户是否有权限访问该家庭
        if (!hasAccessToFamily(user, family)) {
            throw new BusinessException("您没有权限在该家庭发布动态");
        }

        // 创建家庭动态
        FamilyPost post = new FamilyPost();
        post.setFamily(family);
        post.setAuthor(user);
        post.setContent(request.getContent());
        post.setImages(objectMapper.writeValueAsString(request.getImages()));
        post.setVideos(objectMapper.writeValueAsString(request.getVideos()));
        post.setLikes("[]"); // 初始化为空数组
        post.setComments("[]"); // 初始化为空数组
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setViewCount(0);

        FamilyPost savedPost = familyPostRepository.save(post);
        log.info("成功创建家庭动态: id={}", savedPost.getId());

        return convertToPostResponse(savedPost);
    }

    /**
     * 获取家庭的动态
     */
    public Page<FamilyPostResponse> getFamilyPosts(String username, Long familyId, int page, int size) {
        log.info("获取家庭动态: username={}, familyId={}, page={}, size={}", username, familyId, page, size);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找家庭
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BusinessException("家庭不存在"));

        // 验证用户是否有权限访问该家庭
        if (!hasAccessToFamily(user, family)) {
            throw new BusinessException("您没有权限查看该家庭的动态");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<FamilyPost> posts = familyPostRepository.findByFamilyOrderByCreatedAtDesc(family, pageable);

        return posts.map(this::convertToPostResponse);
    }

    /**
     * 点赞动态
     */
    @Transactional
    public FamilyPostResponse likePost(String username, Long postId) throws JsonProcessingException {
        log.info("点赞动态: username={}, postId={}", username, postId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找动态
        FamilyPost post = familyPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("动态不存在"));

        // 验证权限
        if (!hasAccessToFamily(user, post.getFamily())) {
            throw new BusinessException("您没有权限操作该动态");
        }

        // 获取当前点赞用户列表
        List<Long> likedUserIds = getLikedUserIds(post.getLikes());
        
        // 如果用户已经点赞，则取消点赞；否则添加点赞
        if (likedUserIds.contains(user.getId())) {
            likedUserIds.remove(user.getId());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        } else {
            likedUserIds.add(user.getId());
            post.setLikeCount(post.getLikeCount() + 1);
        }
        
        // 更新点赞列表
        post.setLikes(objectMapper.writeValueAsString(likedUserIds));
        FamilyPost savedPost = familyPostRepository.save(post);

        return convertToPostResponse(savedPost);
    }

    /**
     * 取消点赞
     */
    @Transactional
    public FamilyPostResponse unlikePost(String username, Long postId) throws JsonProcessingException {
        log.info("取消点赞: username={}, postId={}", username, postId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找动态
        FamilyPost post = familyPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("动态不存在"));

        // 验证权限
        if (!hasAccessToFamily(user, post.getFamily())) {
            throw new BusinessException("您没有权限操作该动态");
        }

        // 获取当前点赞用户列表
        List<Long> likedUserIds = getLikedUserIds(post.getLikes());
        
        // 如果用户已点赞，则取消点赞
        if (likedUserIds.contains(user.getId())) {
            likedUserIds.remove(user.getId());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            post.setLikes(objectMapper.writeValueAsString(likedUserIds));
            FamilyPost savedPost = familyPostRepository.save(post);
            return convertToPostResponse(savedPost);
        }

        return convertToPostResponse(post);
    }

    /**
     * 删除动态
     */
    @Transactional
    public void deletePost(String username, Long postId) {
        log.info("删除动态: username={}, postId={}", username, postId);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 查找动态
        FamilyPost post = familyPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("动态不存在"));

        // 验证权限（只有作者或家庭创建者可以删除）
        if (!post.getAuthor().getId().equals(user.getId()) && 
            !isFamilyCreator(user, post.getFamily())) {
            throw new BusinessException("您没有权限删除该动态");
        }

        familyPostRepository.delete(post);
        log.info("成功删除动态: id={}", postId);
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
     * 从JSON字符串解析点赞用户ID列表
     */
    private List<Long> getLikedUserIds(String likesJson) {
        if (likesJson == null || likesJson.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            return objectMapper.readValue(likesJson, new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            log.error("解析点赞用户列表失败: {}", likesJson, e);
            return new ArrayList<>();
        }
    }

    /**
     * 转换为动态响应DTO
     */
    private FamilyPostResponse convertToPostResponse(FamilyPost post) {
        FamilyPostResponse response = new FamilyPostResponse();
        response.setId(post.getId());
        response.setFamilyId(post.getFamily().getId());
        response.setFamilyName(post.getFamily().getName());
        response.setAuthorId(post.getAuthor().getId());
        response.setAuthorUsername(post.getAuthor().getUsername());
        response.setAuthorNickname(post.getAuthor().getNickname());
        response.setContent(post.getContent());
        
        // 解析图片和视频列表
        try {
            if (post.getImages() != null) {
                response.setImages(objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {}));
            }
            if (post.getVideos() != null) {
                response.setVideos(objectMapper.readValue(post.getVideos(), new TypeReference<List<String>>() {}));
            }
        } catch (JsonProcessingException e) {
            log.error("解析媒体文件列表失败", e);
        }
        
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(post.getCommentCount());
        response.setViewCount(post.getViewCount());
        response.setPostType(post.getPostType());
        response.setIsPinned(post.getIsPinned());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());

        return response;
    }
}