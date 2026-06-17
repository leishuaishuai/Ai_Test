
package com.language.learn.controller;

import com.language.learn.dto.request.LoginRequest;
import com.language.learn.dto.request.RegisterRequest;
import com.language.learn.dto.response.LoginResponse;
import com.language.learn.dto.response.UserResponse;
import com.language.learn.entity.User;
import com.language.learn.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(Map.of("code", 200, "message", "登录成功", "data", response));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.ok(Map.of("code", 200, "message", "注册成功", "data", response));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.getCurrentUser(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", response));
    }

    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateCurrentUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.updateUser(userId, user);
        return ResponseEntity.ok(Map.of("code", 200, "message", "更新成功", "data", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        return ResponseEntity.ok(Map.of("code", 200, "message", "退出成功"));
    }
}
