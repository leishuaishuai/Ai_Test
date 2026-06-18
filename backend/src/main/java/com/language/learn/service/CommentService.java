
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.entity.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService extends IService<Comment> {

    List<Map<String, Object>> getCommentsByPost(Long postId, Integer page, Integer size);

    Comment createComment(Long userId, Long postId, Long parentId, String content);

    Comment updateComment(Long id, String content);

    void deleteComment(Long id);
}
