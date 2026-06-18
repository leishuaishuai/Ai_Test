
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.Comment;
import com.language.learn.entity.Post;
import com.language.learn.mapper.CommentMapper;
import com.language.learn.mapper.PostMapper;
import com.language.learn.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final PostMapper postMapper;

    public CommentServiceImpl(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    public List<Comment> getCommentsByPost(Long postId, Integer page, Integer size) {
        Page<Comment> pageResult = page(new Page<>(page, size),
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .eq(Comment::getParentId, 0)
                        .orderByDesc(Comment::getCreatedAt));
        return pageResult.getRecords();
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
