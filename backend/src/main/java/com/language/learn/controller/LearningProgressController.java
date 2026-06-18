
package com.language.learn.controller;

import com.language.learn.entity.UserCourseProgress;
import com.language.learn.entity.UserLessonProgress;
import com.language.learn.entity.UserWordProgress;
import com.language.learn.service.LearningProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning")
public class LearningProgressController {

    private final LearningProgressService learningProgressService;

    public LearningProgressController(LearningProgressService learningProgressService) {
        this.learningProgressService = learningProgressService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> stats = learningProgressService.getLearningStatistics(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", stats));
    }

    @GetMapping("/course-progress")
    public ResponseEntity<Map<String, Object>> getUserCourseProgress() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        List<UserCourseProgress> progresses = learningProgressService.getUserCourseProgress(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", progresses));
    }

    @GetMapping("/course-progress/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseProgress(@PathVariable Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        UserCourseProgress progress = learningProgressService.getCourseProgress(userId, courseId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", progress));
    }

    @PostMapping("/course-progress/{courseId}")
    public ResponseEntity<Map<String, Object>> updateCourseProgress(@PathVariable Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        UserCourseProgress progress = learningProgressService.updateCourseProgress(userId, courseId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "更新成功", "data", progress));
    }

    @GetMapping("/lesson-progress/{lessonId}")
    public ResponseEntity<Map<String, Object>> getLessonProgress(@PathVariable Long lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        UserLessonProgress progress = learningProgressService.getLessonProgress(userId, lessonId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", progress));
    }

    @PostMapping("/lesson-progress/{lessonId}")
    public ResponseEntity<Map<String, Object>> updateLessonProgress(
            @PathVariable Long lessonId,
            @RequestBody Map<String, Integer> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        UserLessonProgress progress = learningProgressService.updateLessonProgress(
                userId, lessonId, request.get("score"), request.get("learnTime"));
        return ResponseEntity.ok(Map.of("code", 200, "message", "更新成功", "data", progress));
    }

    @GetMapping("/word-progress/{wordId}")
    public ResponseEntity<Map<String, Object>> getWordProgress(@PathVariable Long wordId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        UserWordProgress progress = learningProgressService.getWordProgress(userId, wordId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", progress));
    }

    @PostMapping("/word-progress/{wordId}")
    public ResponseEntity<Map<String, Object>> updateWordProgress(
            @PathVariable Long wordId,
            @RequestBody Map<String, Object> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        
        UserWordProgress progress;
        if (request.containsKey("progress")) {
            Integer progressValue = (Integer) request.get("progress");
            progress = learningProgressService.updateWordProgress(userId, wordId, progressValue);
        } else {
            Boolean isCorrect = (Boolean) request.get("isCorrect");
            progress = learningProgressService.updateWordProgress(userId, wordId, isCorrect);
        }
        return ResponseEntity.ok(Map.of("code", 200, "message", "更新成功", "data", progress));
    }

    @GetMapping("/word-progress/list")
    public ResponseEntity<Map<String, Object>> getWordProgressList(@RequestParam Long languageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        List<UserWordProgress> progresses = learningProgressService.getWordProgressList(userId, languageId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", progresses));
    }

    @GetMapping("/daily-statistics")
    public ResponseEntity<Map<String, Object>> getDailyStatistics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> stats = learningProgressService.getDailyStatistics(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", stats));
    }

    @GetMapping("/weekly-statistics")
    public ResponseEntity<Map<String, Object>> getWeeklyStatistics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> stats = learningProgressService.getWeeklyStatistics(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", stats));
    }

    @GetMapping("/word-stats")
    public ResponseEntity<Map<String, Object>> getWordStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> stats = learningProgressService.getLearningStatistics(userId);
        
        Map<String, Object> wordStats = new HashMap<>();
        wordStats.put("mastered", stats.get("masteredWords"));
        wordStats.put("learning", stats.get("learningWords"));
        wordStats.put("new", stats.get("newWords"));
        
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", wordStats));
    }

    @GetMapping("/review-words-count")
    public ResponseEntity<Map<String, Object>> getReviewWordsCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        int count = learningProgressService.getReviewWordsCount(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", count));
    }

    @GetMapping("/review-recommendations")
    public ResponseEntity<Map<String, Object>> getReviewRecommendations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        List<Map<String, Object>> recommendations = learningProgressService.getWordsForReviewRecommendation(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", recommendations));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        List<Map<String, Object>> recommendations = learningProgressService.getRecommendedCourses(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", recommendations));
    }

    @GetMapping("/review-words")
    public ResponseEntity<Map<String, Object>> getReviewWordsRecommendation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        List<Map<String, Object>> reviewWords = learningProgressService.getWordsForReviewRecommendation(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", reviewWords));
    }

    @GetMapping("/review-words/count")
    public ResponseEntity<Map<String, Object>> getReviewWordsCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        int count = learningProgressService.getReviewWordsCount(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", count));
    }
}
