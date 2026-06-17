
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.Post;
import com.language.learn.entity.PostLike;
import com.language.learn.mapper.PostLikeMapper;
import com.language.learn.mapper.PostMapper;
import com.language.learn.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final PostLikeMapper postLikeMapper;

    @Override
    public List<Post> getPosts(Integer page, Integer size, Long languageId) {
        Page<Post> pageResult = page(new Page<>(page, size),
                new LambdaQueryWrapper<Post>()
                        .eq(languageId != null, Post::getLanguageId, languageId)
                        .eq(Post::getStatus, 1)
                        .orderByDesc(Post::getCreatedAt));
        return pageResult.getRecords();
    }

    @Override
    public Post getPostById(Long id) {
        Post post = getById(id);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            updateById(post);
        }
        return post;
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
