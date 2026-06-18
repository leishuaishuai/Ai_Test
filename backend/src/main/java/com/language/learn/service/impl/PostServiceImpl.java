
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.Post;
import com.language.learn.entity.PostLike;
import com.language.learn.entity.User;
import com.language.learn.mapper.PostLikeMapper;
import com.language.learn.mapper.PostMapper;
import com.language.learn.mapper.UserMapper;
import com.language.learn.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostLikeMapper postLikeMapper;
    private final UserMapper userMapper;

    public PostServiceImpl(PostLikeMapper postLikeMapper, UserMapper userMapper) {
        this.postLikeMapper = postLikeMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<Map<String, Object>> getPosts(Integer page, Integer size, Long languageId) {
        Page<Post> pageResult = page(new Page<>(page, size),
                new LambdaQueryWrapper<Post>()
                        .eq(languageId != null, Post::getLanguageId, languageId)
                        .eq(Post::getStatus, 1)
                        .orderByDesc(Post::getCreatedAt));
        
        return pageResult.getRecords().stream()
                .map(this::convertPostToMap)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getPostById(Long id) {
        Post post = getById(id);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            updateById(post);
            return convertPostToMap(post);
        }
        return null;
    }

    private Map<String, Object> convertPostToMap(Post post) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", post.getId());
        map.put("title", post.getTitle());
        map.put("content", post.getContent());
        map.put("image", post.getImageUrls());
        map.put("languageId", post.getLanguageId());
        map.put("viewCount", post.getViewCount());
        map.put("likeCount", post.getLikeCount());
        map.put("commentCount", post.getCommentCount());
        map.put("createdAt", formatTime(post.getCreatedAt()));
        
        // 获取作者信息
        User author = userMapper.selectById(post.getUserId());
        if (author != null) {
            map.put("authorId", author.getId());
            map.put("authorName", author.getNickname() != null ? author.getNickname() : author.getUsername());
            map.put("authorAvatar", author.getAvatar());
        } else {
            map.put("authorId", post.getUserId());
            map.put("authorName", "未知用户");
            map.put("authorAvatar", null);
        }
        
        return map;
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long diffMinutes = java.time.Duration.between(time, now).toMinutes();
        
        if (diffMinutes < 60) {
            return diffMinutes + "分钟前";
        } else if (diffMinutes < 24 * 60) {
            return (diffMinutes / 60) + "小时前";
        } else if (diffMinutes < 7 * 24 * 60) {
            return (diffMinutes / (24 * 60)) + "天前";
        } else {
            return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    @Override
    @Transactional
    public Post createPost(Long userId, Post post) {
        post.setUserId(userId);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(1);
        save(post);
        log.info("用户 {} 创建帖子: {}", userId, post.getTitle());
        return post;
    }

    @Override
    @Transactional
    public Post updatePost(Long id, Post post) {
        Post existing = getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
        
        existing.setTitle(post.getTitle());
        existing.setContent(post.getContent());
        existing.setImageUrls(post.getImageUrls());
        existing.setLanguageId(post.getLanguageId());
        
        updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = getById(id);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional
    public void likePost(Long userId, Long postId) {
        PostLike existing = postLikeMapper.selectOne(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getUserId, userId)
                .eq(PostLike::getPostId, postId));
        
        if (existing == null) {
            PostLike postLike = new PostLike();
            postLike.setUserId(userId);
            postLike.setPostId(postId);
            postLikeMapper.insert(postLike);
            
            Post post = getById(postId);
            if (post != null) {
                post.setLikeCount(post.getLikeCount() + 1);
                updateById(post);
            }
        }
    }

    @Override
    @Transactional
    public void unlikePost(Long userId, Long postId) {
        PostLike existing = postLikeMapper.selectOne(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getUserId, userId)
                .eq(PostLike::getPostId, postId));
        
        if (existing != null) {
            postLikeMapper.deleteById(existing.getId());
            
            Post post = getById(postId);
            if (post != null) {
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                updateById(post);
            }
        }
    }

    @Override
    public boolean isLiked(Long userId, Long postId) {
        return postLikeMapper.selectCount(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getUserId, userId)
                .eq(PostLike::getPostId, postId)) > 0;
    }
}
