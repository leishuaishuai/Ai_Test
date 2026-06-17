
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.*;
import com.language.learn.mapper.*;
import com.language.learn.service.AchievementService;
import com.language.learn.service.LearningProgressService;
import com.language.learn.service.UserPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningProgressServiceImpl implements LearningProgressService {

    private final UserCourseProgressMapper userCourseProgressMapper;
    private final UserLessonProgressMapper userLessonProgressMapper;
    private final UserWordProgressMapper userWordProgressMapper;
    private final ChapterMapper chapterMapper;
    private final LessonMapper lessonMapper;
    private final CourseMapper courseMapper;
    private final WordMapper wordMapper;
    private final AchievementService achievementService;
    private final UserPointsService userPointsService;

    @Override
    public UserCourseProgress getCourseProgress(Long userId, Long courseId) {
        return userCourseProgressMapper.selectOne(new LambdaQueryWrapper<UserCourseProgress>()
                .eq(UserCourseProgress::getUserId, userId)
                .eq(UserCourseProgress::getCourseId, courseId));
    }

    @Override
    public List<UserCourseProgress> getUserCourseProgress(Long userId) {
        List<UserCourseProgress> progresses = userCourseProgressMapper.selectList(
                new LambdaQueryWrapper<UserCourseProgress>()
                        .eq(UserCourseProgress::getUserId, userId)
                        .orderByDesc(UserCourseProgress::getLastLearnTime));
        
        for (UserCourseProgress progress : progresses) {
            Course course = courseMapper.selectById(progress.getCourseId());
            if (course != null) {
                progress.setProgressPercent(calculateProgress(course.getId(), userId));
            }
        }
        
        return progresses;
    }

    @Override
    @Transactional
    public UserCourseProgress updateCourseProgress(Long userId, Long courseId) {
        UserCourseProgress progress = userCourseProgressMapper.selectOne(new LambdaQueryWrapper<UserCourseProgress>()
                .eq(UserCourseProgress::getUserId, userId)
                .eq(UserCourseProgress::getCourseId, courseId));
        
        if (progress == null) {
            progress = new UserCourseProgress();
            progress.setUserId(userId);
            progress.setCourseId(courseId);
            progress.setCompletedChapters(0);
            progress.setCompletedLessons(0);
            progress.setProgressPercent(BigDecimal.ZERO);
            progress.setIsCompleted(0);
            userCourseProgressMapper.insert(progress);
        }
        
        Course course = courseMapper.selectById(courseId);
        if (course != null) {
            progress.setProgressPercent(calculateProgress(courseId, userId));
            
            List<Chapter> chapters = chapterMapper.selectList(new LambdaQueryWrapper<Chapter>()
                    .eq(Chapter::getCourseId, courseId)
                    .eq(Chapter::getStatus, 1));
            
            int completedChapters = 0;
            int totalLessons = 0;
            int completedLessons = 0;
            
            for (Chapter chapter : chapters) {
                List<Lesson> lessons = lessonMapper.selectList(new LambdaQueryWrapper<Lesson>()
                        .eq(Lesson::getChapterId, chapter.getId())
                        .eq(Lesson::getStatus, 1));
                totalLessons += lessons.size();
                
                boolean chapterCompleted = true;
                for (Lesson lesson : lessons) {
                    UserLessonProgress lessonProgress = userLessonProgressMapper.selectOne(
                            new LambdaQueryWrapper<UserLessonProgress>()
                                    .eq(UserLessonProgress::getUserId, userId)
                                    .eq(UserLessonProgress::getLessonId, lesson.getId()));
                    if (lessonProgress != null && lessonProgress.getIsCompleted() == 1) {
                        completedLessons++;
                    } else {
                        chapterCompleted = false;
                    }
                }
                if (chapterCompleted) {
                    completedChapters++;
                }
            }
            
            progress.setCompletedChapters(completedChapters);
            progress.setCompletedLessons(completedLessons);
            progress.setLastLearnTime(LocalDateTime.now());
            progress.setIsCompleted(completedLessons == totalLessons && totalLessons > 0 ? 1 : 0);
            
            userCourseProgressMapper.updateById(progress);
            
            if (progress.getIsCompleted() == 1) {
                achievementService.checkAchievements(userId, 2, 1);
                userPointsService.addPoints(userId, 100, 1, "完成课程: " + course.getTitle());
            }
        }
        
        return progress;
    }

    @Override
    public UserLessonProgress getLessonProgress(Long userId, Long lessonId) {
        return userLessonProgressMapper.selectOne(new LambdaQueryWrapper<UserLessonProgress>()
                .eq(UserLessonProgress::getUserId, userId)
                .eq(UserLessonProgress::getLessonId, lessonId));
    }

    @Override
    @Transactional
    public UserLessonProgress updateLessonProgress(Long userId, Long lessonId, Integer score, Integer learnTime) {
        UserLessonProgress progress = userLessonProgressMapper.selectOne(new LambdaQueryWrapper<UserLessonProgress>()
                .eq(UserLessonProgress::getUserId, userId)
                .eq(UserLessonProgress::getLessonId, lessonId));
        
        if (progress == null) {
            progress = new UserLessonProgress();
            progress.setUserId(userId);
            progress.setLessonId(lessonId);
            progress.setIsCompleted(0);
            progress.setScore(0);
            progress.setLearnTime(0);
            userLessonProgressMapper.insert(progress);
        }
        
        boolean wasCompleted = progress.getIsCompleted() == 1;
        
        if (score != null && score >= 60 && !wasCompleted) {
            progress.setIsCompleted(1);
        }
        if (score != null) {
            progress.setScore(Math.max(progress.getScore(), score));
        }
        if (learnTime != null) {
            progress.setLearnTime(progress.getLearnTime() + learnTime);
        }
        progress.setLastLearnTime(LocalDateTime.now());
        
        userLessonProgressMapper.updateById(progress);
        
        if (!wasCompleted && progress.getIsCompleted() == 1) {
            Lesson lesson = lessonMapper.selectById(lessonId);
            if (lesson != null) {
                achievementService.checkAchievements(userId, 2, 1);
                userPointsService.addPoints(userId, 10, 1, "完成课时: " + lesson.getTitle());
            }
        }
        
        userPointsService.addPoints(userId, learnTime / 60, 1, "学习时长奖励");
        
        return progress;
    }

    @Override
    public UserWordProgress getWordProgress(Long userId, Long wordId) {
        return userWordProgressMapper.selectOne(new LambdaQueryWrapper<UserWordProgress>()
                .eq(UserWordProgress::getUserId, userId)
                .eq(UserWordProgress::getWordId, wordId));
    }

    @Override
    @Transactional
    public UserWordProgress updateWordProgress(Long userId, Long wordId, Boolean isCorrect) {
        UserWordProgress progress = userWordProgressMapper.selectOne(new LambdaQueryWrapper<UserWordProgress>()
                .eq(UserWordProgress::getUserId, userId)
                .eq(UserWordProgress::getWordId, wordId));
        
        if (progress == null) {
            progress = new UserWordProgress();
            progress.setUserId(userId);
            progress.setWordId(wordId);
            progress.setStatus(0);
            progress.setCorrectCount(0);
            progress.setWrongCount(0);
            userWordProgressMapper.insert(progress);
        }
        
        if (Boolean.TRUE.equals(isCorrect)) {
            progress.setCorrectCount(progress.getCorrectCount() + 1);
            if (progress.getStatus() == 0) {
                progress.setStatus(1);
            }
            if (progress.getCorrectCount() >= 5) {
                progress.setStatus(2);
            }
            
            int daysToAdd = switch (progress.getCorrectCount()) {
                case 1 -> 1;
                case 2 -> 3;
                case 3 -> 7;
                case 4 -> 14;
                default -> 30;
            };
            progress.setNextReviewTime(LocalDateTime.now().plusDays(daysToAdd));
        } else {
            progress.setWrongCount(progress.getWrongCount() + 1);
            if (progress.getStatus() == 2) {
                progress.setStatus(1);
            }
            progress.setNextReviewTime(LocalDateTime.now().plusHours(1));
        }
        
        progress.setLastReviewTime(LocalDateTime.now());
        userWordProgressMapper.updateById(progress);
        
        long masteredCount = userWordProgressMapper.selectCount(new LambdaQueryWrapper<UserWordProgress>()
                .eq(UserWordProgress::getUserId, userId)
                .eq(UserWordProgress::getStatus, 2));
        achievementService.checkAchievements(userId, 3, (int) masteredCount);
        
        return progress;
    }

    @Override
    public Map<String, Object> getLearningStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalLearnTime = userLessonProgressMapper.selectList(
                new LambdaQueryWrapper<UserLessonProgress>()
                        .eq(UserLessonProgress::getUserId, userId))
                .stream()
                .mapToInt(UserLessonProgress::getLearnTime)
                .sum();
        
        long completedCourses = userCourseProgressMapper.selectCount(
                new LambdaQueryWrapper<UserCourseProgress>()
                        .eq(UserCourseProgress::getUserId, userId)
                        .eq(UserCourseProgress::getIsCompleted, 1));
        
        long completedLessons = userLessonProgressMapper.selectCount(
                new LambdaQueryWrapper<UserLessonProgress>()
                        .eq(UserLessonProgress::getUserId, userId)
                        .eq(UserLessonProgress::getIsCompleted, 1));
        
        long masteredWords = userWordProgressMapper.selectCount(
                new LambdaQueryWrapper<UserWordProgress>()
                        .eq(UserWordProgress::getUserId, userId)
                        .eq(UserWordProgress::getStatus, 2));
        
        long learningWords = userWordProgressMapper.selectCount(
                new LambdaQueryWrapper<UserWordProgress>()
                        .eq(UserWordProgress::getUserId, userId)
                        .eq(UserWordProgress::getStatus, 1));
        
        stats.put("totalLearnTime", totalLearnTime);
        stats.put("completedCourses", completedCourses);
        stats.put("completedLessons", completedLessons);
        stats.put("masteredWords", masteredWords);
        stats.put("learningWords", learningWords);
        
        achievementService.checkAchievements(userId, 1, (int) totalLearnTime / 60);
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getRecommendedCourses(Long userId) {
        List<UserCourseProgress> userProgresses = userCourseProgressMapper.selectList(
                new LambdaQueryWrapper<UserCourseProgress>()
                        .eq(UserCourseProgress::getUserId, userId));
        
        Set<Long> enrolledCourseIds = userProgresses.stream()
                .map(UserCourseProgress::getCourseId)
                .collect(Collectors.toSet());
        
        List<Course> allCourses = courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
                .notIn(Course::getId, enrolledCourseIds)
                .orderByDesc(Course::getCreatedAt)
                .last("LIMIT 5"));
        
        return allCourses.stream()
                .map(course -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", course.getId());
                    map.put("title", course.getTitle());
                    map.put("level", course.getLevel());
                    map.put("coverImage", course.getCoverImage());
                    map.put("price", course.getPrice());
                    map.put("isFree", course.getIsFree());
                    return map;
                })
                .collect(Collectors.toList());
    }

    private BigDecimal calculateProgress(Long courseId, Long userId) {
        List<Chapter> chapters = chapterMapper.selectList(new LambdaQueryWrapper<Chapter>()
                .eq(Chapter::getCourseId, courseId)
                .eq(Chapter::getStatus, 1));
        
        int totalLessons = 0;
        int completedLessons = 0;
        
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonMapper.selectList(new LambdaQueryWrapper<Lesson>()
                    .eq(Lesson::getChapterId, chapter.getId())
                    .eq(Lesson::getStatus, 1));
            totalLessons += lessons.size();
            
            for (Lesson lesson : lessons) {
                UserLessonProgress lessonProgress = userLessonProgressMapper.selectOne(
                        new LambdaQueryWrapper<UserLessonProgress>()
                                .eq(UserLessonProgress::getUserId, userId)
                                .eq(UserLessonProgress::getLessonId, lesson.getId()));
                if (lessonProgress != null && lessonProgress.getIsCompleted() == 1) {
                    completedLessons++;
                }
            }
        }
        
        if (totalLessons == 0) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(completedLessons * 100.0 / totalLessons).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
