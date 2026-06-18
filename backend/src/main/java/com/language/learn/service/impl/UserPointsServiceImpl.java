
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.UserPoints;
import com.language.learn.mapper.UserPointsMapper;
import com.language.learn.service.UserPointsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserPointsServiceImpl extends ServiceImpl<UserPointsMapper, UserPoints> implements UserPointsService {

    private static final Logger log = LoggerFactory.getLogger(UserPointsServiceImpl.class);

    @Override
    public Integer getUserTotalPoints(Long userId) {
        Integer total = baseMapper.selectSumByUserId(userId);
        return total != null ? total : 0;
    }

    @Override
    @Transactional
    public void addPoints(Long userId, Integer points, Integer type, String description) {
        UserPoints userPoints = new UserPoints();
        userPoints.setUserId(userId);
        userPoints.setPoints(points);
        userPoints.setType(type);
        userPoints.setDescription(description);
        save(userPoints);
        log.info("用户 {} 获得积分: {} ({})", userId, points, description);
    }

    @Override
    public List<UserPoints> getUserPointsHistory(Long userId, Integer page, Integer size) {
        Page<UserPoints> pageResult = page(new Page<>(page, size),
                new LambdaQueryWrapper<UserPoints>()
                        .eq(UserPoints::getUserId, userId)
                        .orderByDesc(UserPoints::getCreatedAt));
        return pageResult.getRecords();
    }
}
