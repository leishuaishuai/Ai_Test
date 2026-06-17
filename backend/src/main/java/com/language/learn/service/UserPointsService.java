
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.entity.UserPoints;

import java.util.List;

public interface UserPointsService extends IService<UserPoints> {

    Integer getUserTotalPoints(Long userId);

    void addPoints(Long userId, Integer points, Integer type, String description);

    List<UserPoints> getUserPointsHistory(Long userId, Integer page, Integer size);
}
