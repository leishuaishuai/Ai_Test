
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.entity.Achievement;
import com.language.learn.entity.UserAchievement;

import java.util.List;

public interface AchievementService extends IService<Achievement> {

    List<Achievement> getAllAchievements();

    List<UserAchievement> getUserAchievements(Long userId);

    void checkAchievements(Long userId, Integer type, Integer value);

    Achievement createAchievement(Achievement achievement);

    Achievement updateAchievement(Long id, Achievement achievement);

    void deleteAchievement(Long id);
}
