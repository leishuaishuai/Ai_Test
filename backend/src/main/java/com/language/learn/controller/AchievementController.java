
package com.language.learn.controller;

import com.language.learn.entity.Achievement;
import com.language.learn.entity.UserAchievement;
import com.language.learn.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAchievements() {
        List<Achievement> achievements = achievementService.getAllAchievements();
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", achievements));
    }

    @GetMapping("/mine")
    public ResponseEntity<Map<String, Object>> getUserAchievements() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        List<UserAchievement> achievements = achievementService.getUserAchievements(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", achievements));
    }
}
