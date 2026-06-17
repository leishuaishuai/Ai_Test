
package com.language.learn.controller;

import com.language.learn.entity.Post;
import com.language.learn.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long languageId) {
        List<Post> posts = postService.getPosts(page, size, languageId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", posts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        boolean isLiked = postService.isLiked(userId, id);
        
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", Map.of(
                "post", post,
                "isLiked", isLiked
        )));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Post post) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        Post created = postService.createPost(userId, post);
        return ResponseEntity.ok(Map.of("code", 200, "message", "创建成功", "data", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable Long id, @RequestBody Post post) {
        Post updated = postService.updatePost(id, post);
        return ResponseEntity.ok(Map.of("code", 200, "message", "更新成功", "data", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(Map.of("code", 200, "message", "删除成功"));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        postService.likePost(userId, id);
        return ResponseEntity.ok(Map.of("code", 200, "message", "点赞成功"));
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<Map<String, Object>> unlikePost(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        postService.unlikePost(userId, id);
        return ResponseEntity.ok(Map.of("code", 200, "message", "取消点赞"));
    }
}
