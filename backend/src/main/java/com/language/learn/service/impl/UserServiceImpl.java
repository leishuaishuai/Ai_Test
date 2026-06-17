
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.dto.request.LoginRequest;
import com.language.learn.dto.request.RegisterRequest;
import com.language.learn.dto.response.LoginResponse;
import com.language.learn.dto.response.UserResponse;
import com.language.learn.entity.User;
import com.language.learn.mapper.UserMapper;
import com.language.learn.service.UserService;
import com.language.learn.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = getByUsername(request.getUsername());
        
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw new IllegalArgumentException("用户已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        if (request.getEmail() != null && existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        
        if (request.getPhone() != null && existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("手机号已被注册");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole(0);
        user.setStatus(1);
        
        save(user);
        log.info("用户注册成功: {}", user.getUsername());
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public UserResponse getCurrentUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .nickname(user.getNickname())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, User user) {
        User existingUser = getById(userId);
        if (existingUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail()) && existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已被使用");
        }
        
        if (user.getPhone() != null && !user.getPhone().equals(existingUser.getPhone()) && existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("手机号已被使用");
        }
        
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setNickname(user.getNickname());
        existingUser.setAvatar(user.getAvatar());
        
        updateById(existingUser);
        
        return getCurrentUser(userId);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!existsById(userId)) {
            throw new IllegalArgumentException("用户不存在");
        }
        removeById(userId);
    }

    private User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0));
    }

    private boolean existsByUsername(String username) {
        return count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0)) > 0;
    }

    private boolean existsByEmail(String email) {
        return count(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
                .eq(User::getDeleted, 0)) > 0;
    }

    private boolean existsByPhone(String phone) {
        return count(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
                .eq(User::getDeleted, 0)) > 0;
    }
}
