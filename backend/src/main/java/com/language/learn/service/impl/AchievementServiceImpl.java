
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.Achievement;
import com.language.learn.entity.UserAchievement;
import com.language.learn.mapper.AchievementMapper;
import com.language.learn.mapper.UserAchievementMapper;
import com.language.learn.service.AchievementService;
import com.language.learn.service.UserPointsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AchievementServiceImpl extends ServiceImpl<AchievementMapper, Achievement> implements AchievementService {

    private static final Logger log = LoggerFactory.getLogger(AchievementServiceImpl.class);

    private final UserAchievementMapper userAchievementMapper;
    private final UserPointsService userPointsService;

    public AchievementServiceImpl(UserAchievementMapper userAchievementMapper, UserPointsService userPointsService) {
        this.userAchievementMapper = userAchievementMapper;
        this.userPointsService = userPointsService;
    }

    @Override
    public List<Achievement> getAllAchievements() {
        return list(new LambdaQueryWrapper<Achievement>()
                .eq(Achievement::getStatus, 1)
                .orderByAsc(Achievement::getSortOrder));
    }

    @Override
    public List<UserAchievement> getUserAchievements(Long userId) {
        return userAchievementMapper.selectList(new LambdaQueryWrapper<UserAchievement>()
                .eq(UserAchievement::getUserId, userId));
    }

    @Override
    @Transactional
    public void checkAchievements(Long userId, Integer type, Integer value) {
        List<Achievement> achievements = list(new LambdaQueryWrapper<Achievement>()
                .eq(Achievement::getType, type)
                .eq(Achievement::getStatus, 1));
        
        for (Achievement achievement : achievements) {
            UserAchievement userAchievement = userAchievementMapper.selectOne(new LambdaQueryWrapper<UserAchievement>()
                    .eq(UserAchievement::getUserId, userId)
                    .eq(UserAchievement::getAchievementId, achievement.getId()));
            
            if (userAchievement == null) {
                userAchievement = new UserAchievement();
                userAchievement.setUserId(userId);
                userAchievement.setAchievementId(achievement.getId());
                userAchievement.setIsUnlocked(0);
                userAchievementMapper.insert(userAchievement);
            }
            
            if (userAchievement.getIsUnlocked() == 0 && value >= achievement.getConditionValue()) {
                userAchievement.setIsUnlocked(1);
                userAchievement.setUnlockedAt(LocalDateTime.now());
                userAchievementMapper.updateById(userAchievement);
                
                userPointsService.addPoints(userId, achievement.getPoints(), 2, "解锁成就: " + achievement.getTitle());
                log.info("用户 {} 解锁成就: {}", userId, achievement.getTitle());
            }
        }
    }

    @Override
    @Transactional
    public Achievement createAchievement(Achievement achievement) {
        save(achievement);
        log.info("创建成就成功: {}", achievement.getTitle());
        return achievement;
    }

    @Override
    @Transactional
    public Achievement updateAchievement(Long id, Achievement achievement) {
        Achievement existing = getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("成就不存在");
        }
        
        existing.setTitle(achievement.getTitle());
        existing.setDescription(achievement.getDescription());
        existing.setIcon(achievement.getIcon());
        existing.setType(achievement.getType());
        existing.setConditionValue(achievement.getConditionValue());
        existing.setConditionUnit(achievement.getConditionUnit());
        existing.setPoints(achievement.getPoints());
        existing.setStatus(achievement.getStatus());
        
        updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public void deleteAchievement(Long id) {
        if (getById(id) == null) {
            throw new IllegalArgumentException("成就不存在");
        }
        removeById(id);
    }
}
