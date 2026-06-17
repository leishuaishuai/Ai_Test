
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.dto.request.LoginRequest;
import com.language.learn.dto.request.RegisterRequest;
import com.language.learn.dto.response.LoginResponse;
import com.language.learn.dto.response.UserResponse;
import com.language.learn.entity.User;

public interface UserService extends IService<User> {

    LoginResponse login(LoginRequest request);

    UserResponse register(RegisterRequest request);

    UserResponse getCurrentUser(Long userId);

    UserResponse updateUser(Long userId, User user);

    void deleteUser(Long userId);
}
