
package com.language.learn.controller;

import com.language.learn.entity.Comment;
import com.language.learn.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<Comment> comments = commentService.getCommentsByPost(postId, page, size);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", comments));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createComment(
            @RequestBody Map<String, Object> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        Long postId = Long.valueOf(request.get("postId").toString());
        Long parentId = request.containsKey("parentId") ? Long.valueOf(request.get("parentId").toString()) : null;
        String content = request.get("content").toString();
        
        Comment created = commentService.createComment(userId, postId, parentId, content);
        return ResponseEntity.ok(Map.of("code", 200, "message", "评论成功", "data", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Comment updated = commentService.updateComment(id, request.get("content"));
        return ResponseEntity.ok(Map.of("code", 200, "message", "更新成功", "data", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(Map.of("code", 200, "message", "删除成功"));
    }
}
