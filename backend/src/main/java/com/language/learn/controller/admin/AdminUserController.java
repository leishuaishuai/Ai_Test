
package com.language.learn.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.language.learn.dto.response.UserResponse;
import com.language.learn.entity.User;
import com.language.learn.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        
        Page<User> pageResult = userService.page(new Page<>(page, size), 
                new LambdaQueryWrapper<User>()
                        .like(username != null, User::getUsername, username)
                        .eq(status != null, User::getStatus, status)
                        .orderByDesc(User::getCreatedAt));
        
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", Map.of(
                        "records", pageResult.getRecords(),
                        "total", pageResult.getTotal(),
                        "current", pageResult.getCurrent(),
                        "size", pageResult.getSize()
                )
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getCurrentUser(id);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", response));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        User user = userService.getById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "用户不存在"));
        }
        
        user.setStatus(request.get("status"));
        userService.updateById(user);
        
        return ResponseEntity.ok(Map.of("code", 200, "message", "状态更新成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("code", 200, "message", "删除成功"));
    }
}
