
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.UserDailySign;
import com.language.learn.mapper.UserDailySignMapper;
import com.language.learn.service.AchievementService;
import com.language.learn.service.DailySignService;
import com.language.learn.service.UserPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailySignServiceImpl extends ServiceImpl<UserDailySignMapper, UserDailySign> implements DailySignService {

    private final UserPointsService userPointsService;
    private final AchievementService achievementService;

    @Override
    @Transactional
    public Map<String, Object> sign(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        
        if (isSignedToday(userId)) {
            result.put("success", false);
            result.put("message", "今日已签到");
            return result;
        }
        
        UserDailySign signRecord = new UserDailySign();
        signRecord.setUserId(userId);
        signRecord.setSignDate(today);
        save(signRecord);
        
        int continuousDays = getContinuousSignDays(userId);
        int points = switch (continuousDays) {
            case 1 -> 10;
            case 3 -> 30;
            case 7 -> 70;
            case 30 -> 300;
            default -> 10;
        };
        
        userPointsService.addPoints(userId, points, 3, "每日签到，连续" + continuousDays + "天");
        
        achievementService.checkAchievements(userId, 4, continuousDays);
        
        result.put("success", true);
        result.put("message", "签到成功");
        result.put("continuousDays", continuousDays);
        result.put("points", points);
        
        log.info("用户 {} 签到成功，连续 {} 天，获得 {} 积分", userId, continuousDays, points);
        return result;
    }

    @Override
    public boolean isSignedToday(Long userId) {
        LocalDate today = LocalDate.now();
        return count(new LambdaQueryWrapper<UserDailySign>()
                .eq(UserDailySign::getUserId, userId)
                .eq(UserDailySign::getSignDate, today)) > 0;
    }

    @Override
    public int getContinuousSignDays(Long userId) {
        List<UserDailySign> records = list(new LambdaQueryWrapper<UserDailySign>()
                .eq(UserDailySign::getUserId, userId)
                .orderByDesc(UserDailySign::getSignDate)
                .last("LIMIT 30"));
        
        if (records.isEmpty()) {
            return 0;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate lastSignDate = records.get(0).getSignDate();
        
        if (!lastSignDate.equals(today) && !lastSignDate.equals(today.minusDays(1))) {
            return 0;
        }
        
        int count = 1;
        for (int i = 1; i < records.size(); i++) {
            LocalDate current = records.get(i).getSignDate();
            LocalDate previous = records.get(i - 1).getSignDate();
            if (current.equals(previous.minusDays(1))) {
                count++;
            } else {
                break;
            }
        }
        
        return count;
    }

    @Override
    public int getTotalSignDays(Long userId) {
        return (int) count(new LambdaQueryWrapper<UserDailySign>()
                .eq(UserDailySign::getUserId, userId));
    }
}
