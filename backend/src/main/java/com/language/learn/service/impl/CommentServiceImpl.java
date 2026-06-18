
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.Comment;
import com.language.learn.entity.Post;
import com.language.learn.entity.User;
import com.language.learn.mapper.CommentMapper;
import com.language.learn.mapper.PostMapper;
import com.language.learn.mapper.UserMapper;
import com.language.learn.service.CommentService;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final PostMapper postMapper;
    private final UserMapper userMapper;

    public CommentServiceImpl(PostMapper postMapper, UserMapper userMapper) {
        this.postMapper = postMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<Map<String, Object>> getCommentsByPost(Long postId, Integer page, Integer size) {
        Page<Comment> pageResult = page(new Page<>(page, size),
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .eq(Comment::getParentId, 0)
                        .orderByDesc(Comment::getCreatedAt));
        
        return pageResult.getRecords().stream()
                .map(this::convertCommentToMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertCommentToMap(Comment comment) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", comment.getId());
        map.put("postId", comment.getPostId());
        map.put("parentId", comment.getParentId());
        map.put("content", comment.getContent());
        map.put("likeCount", comment.getLikeCount());
        map.put("createdAt", formatTime(comment.getCreatedAt()));
        
        // 获取作者信息
        User author = userMapper.selectById(comment.getUserId());
        if (author != null) {
            map.put("authorId", author.getId());
            map.put("authorName", author.getNickname() != null ? author.getNickname() : author.getUsername());
            map.put("authorAvatar", author.getAvatar());
        } else {
            map.put("authorId", comment.getUserId());
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
    public Comment createComment(Long userId, Long postId, Long parentId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setParentId(parentId != null ? parentId : 0);
        comment.setContent(content);
        comment.setLikeCount(0);
        save(comment);
        
        Post post = postMapper.selectById(postId);
        if (post != null) {
            post.setCommentCount(post.getCommentCount() + 1);
            postMapper.updateById(post);
        }
        
        log.info("用户 {} 评论帖子 {}: {}", userId, postId, content);
        return comment;
    }

    @Override
    @Transactional
    public Comment updateComment(Long id, String content) {
        Comment existing = getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("评论不存在");
        }
        
        existing.setContent(content);
        updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = getById(id);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }
        
        removeById(id);
        
        Post post = postMapper.selectById(comment.getPostId());
        if (post != null) {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postMapper.updateById(post);
        }
    }
}
