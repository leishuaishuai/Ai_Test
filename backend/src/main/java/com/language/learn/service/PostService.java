
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.entity.Post;

import java.util.List;
import java.util.Map;

public interface PostService extends IService<Post> {

    List<Map<String, Object>> getPosts(Integer page, Integer size, Long languageId);

    Map<String, Object> getPostById(Long id);

    Post createPost(Long userId, Post post);

    Post updatePost(Long id, Post post);

    void deletePost(Long id);

    void likePost(Long userId, Long postId);

    void unlikePost(Long userId, Long postId);

    boolean isLiked(Long userId, Long postId);
}
