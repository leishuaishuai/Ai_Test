
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.*;
import com.language.learn.mapper.*;
import com.language.learn.service.AchievementService;
import com.language.learn.service.LearningProgressService;
import com.language.learn.service.UserPointsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearningProgressServiceImpl implements LearningProgressService {

    private static final Logger log = LoggerFactory.getLogger(LearningProgressServiceImpl.class);

    private final UserCourseProgressMapper userCourseProgressMapper;
    private final UserLessonProgressMapper userLessonProgressMapper;
    private final UserWordProgressMapper userWordProgressMapper;
    private final ChapterMapper chapterMapper;
    private final LessonMapper lessonMapper;
    private final CourseMapper courseMapper;
    private final WordMapper wordMapper;
    private final AchievementService achievementService;
    private final UserPointsService userPointsService;

    public LearningProgressServiceImpl(UserCourseProgressMapper userCourseProgressMapper,
                                       UserLessonProgressMapper userLessonProgressMapper,
                                       UserWordProgressMapper userWordProgressMapper,
                                       ChapterMapper chapterMapper,
                                       LessonMapper lessonMapper,
                                       CourseMapper courseMapper,
                                       WordMapper wordMapper,
                                       AchievementService achievementService,
                                       UserPointsService userPointsService) {
        this.userCourseProgressMapper = userCourseProgressMapper;
        this.userLessonProgressMapper = userLessonProgressMapper;
        this.userWordProgressMapper = userWordProgressMapper;
        this.chapterMapper = chapterMapper;
        this.lessonMapper = lessonMapper;
        this.courseMapper = courseMapper;
        this.wordMapper = wordMapper;
        this.achievementService = achievementService;
        this.userPointsService = userPointsService;
    }

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
                // 检查课程完成成就 - type=5，传递已完成的课程总数
                long completedCoursesCount = userCourseProgressMapper.selectCount(
                        new LambdaQueryWrapper<UserCourseProgress>()
                                .eq(UserCourseProgress::getUserId, userId)
                                .eq(UserCourseProgress::getIsCompleted, 1));
                achievementService.checkAchievements(userId, 5, (int) completedCoursesCount);
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
                userPointsService.addPoints(userId, 10, 1, "完成课时: " + lesson.getTitle());
            }
            
            // 检查课时完成成就 - 传递已完成的课时总数
            long completedLessonsCount = userLessonProgressMapper.selectCount(
                    new LambdaQueryWrapper<UserLessonProgress>()
                            .eq(UserLessonProgress::getUserId, userId)
                            .eq(UserLessonProgress::getIsCompleted, 1));
            achievementService.checkAchievements(userId, 2, (int) completedLessonsCount);
        }
        
        // 学习时长奖励积分
        if (learnTime != null && learnTime > 0) {
            userPointsService.addPoints(userId, Math.max(1, learnTime / 60), 1, "学习时长奖励");
        }
        
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
            progress.setProgress(0);
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
            
            // 更新进度百分比
            int newProgress = Math.min(100, (progress.getCorrectCount() * 20));
            progress.setProgress(newProgress);
            
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
            // 错误时降低进度
            int newProgress = Math.max(0, progress.getProgress() - 20);
            progress.setProgress(newProgress);
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
    @Transactional
    public UserWordProgress updateWordProgress(Long userId, Long wordId, Integer progressValue) {
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
            progress.setProgress(0);
            userWordProgressMapper.insert(progress);
        }
        
        // 根据进度值更新状态
        progress.setProgress(progressValue);
        if (progressValue >= 100) {
            progress.setStatus(2); // 已掌握
            progress.setCorrectCount(5);
            progress.setNextReviewTime(LocalDateTime.now().plusDays(30));
        } else if (progressValue >= 50) {
            progress.setStatus(1); // 学习中
            if (progress.getCorrectCount() < 3) {
                progress.setCorrectCount(3);
            }
            progress.setNextReviewTime(LocalDateTime.now().plusDays(7));
        } else if (progressValue > 0) {
            progress.setStatus(1); // 学习中
            if (progress.getCorrectCount() < 1) {
                progress.setCorrectCount(1);
            }
            progress.setNextReviewTime(LocalDateTime.now().plusDays(1));
        } else {
            progress.setStatus(0); // 未学习
            progress.setNextReviewTime(LocalDateTime.now());
        }
        
        progress.setLastReviewTime(LocalDateTime.now());
        userWordProgressMapper.updateById(progress);
        
        return progress;
    }

    @Override
    public List<UserWordProgress> getWordProgressList(Long userId, Long languageId) {
        // 获取该语言下的所有单词
        List<Word> words = wordMapper.selectList(new LambdaQueryWrapper<Word>()
                .eq(Word::getLanguageId, languageId));
        
        List<Long> wordIds = words.stream()
                .map(Word::getId)
                .collect(Collectors.toList());
        
        if (wordIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取用户对这些单词的进度
        List<UserWordProgress> progresses = userWordProgressMapper.selectList(
                new LambdaQueryWrapper<UserWordProgress>()
                        .eq(UserWordProgress::getUserId, userId)
                        .in(UserWordProgress::getWordId, wordIds));
        
        // 为没有进度记录的单词创建默认进度对象
        Map<Long, UserWordProgress> progressMap = progresses.stream()
                .collect(Collectors.toMap(UserWordProgress::getWordId, p -> p));
        
        List<UserWordProgress> result = new ArrayList<>();
        for (Word word : words) {
            UserWordProgress progress = progressMap.get(word.getId());
            if (progress == null) {
                progress = new UserWordProgress();
                progress.setUserId(userId);
                progress.setWordId(word.getId());
                progress.setStatus(0);
                progress.setCorrectCount(0);
                progress.setWrongCount(0);
                progress.setProgress(0);
            }
            result.add(progress);
        }
        
        return result;
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
        
        long newWords = userWordProgressMapper.selectCount(
                new LambdaQueryWrapper<UserWordProgress>()
                        .eq(UserWordProgress::getUserId, userId)
                        .eq(UserWordProgress::getStatus, 0));
        
        // 计算学习天数
        long studyDays = userLessonProgressMapper.selectList(
                new LambdaQueryWrapper<UserLessonProgress>()
                        .eq(UserLessonProgress::getUserId, userId)
                        .select(UserLessonProgress::getLastLearnTime))
                .stream()
                .map(p -> p.getLastLearnTime() != null ? p.getLastLearnTime().toLocalDate() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        
        stats.put("totalLearnTime", totalLearnTime);
        stats.put("totalHours", Math.round(totalLearnTime / 60.0));
        stats.put("completedCourses", completedCourses);
        stats.put("completedLessons", completedLessons);
        stats.put("masteredWords", masteredWords);
        stats.put("learningWords", learningWords);
        stats.put("newWords", newWords);
        stats.put("learnedWords", masteredWords + learningWords);
        stats.put("studyDays", studyDays);
        
        achievementService.checkAchievements(userId, 1, (int) totalLearnTime / 60);
        
        return stats;
    }

    @Override
    public Map<String, Object> getDailyStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        // 今日学习时长
        long todayLearnTime = userLessonProgressMapper.selectList(
                new LambdaQueryWrapper<UserLessonProgress>()
                        .eq(UserLessonProgress::getUserId, userId)
                        .ge(UserLessonProgress::getLastLearnTime, todayStart)
                        .lt(UserLessonProgress::getLastLearnTime, todayEnd))
                .stream()
                .mapToInt(UserLessonProgress::getLearnTime)
                .sum();
        
        // 今日完成课时数
        long todayCompletedLessons = userLessonProgressMapper.selectCount(
                new LambdaQueryWrapper<UserLessonProgress>()
                        .eq(UserLessonProgress::getUserId, userId)
                        .ge(UserLessonProgress::getLastLearnTime, todayStart)
                        .lt(UserLessonProgress::getLastLearnTime, todayEnd)
                        .eq(UserLessonProgress::getIsCompleted, 1));
        
        // 今日学习单词数
        long todayLearnedWords = userWordProgressMapper.selectCount(
                new LambdaQueryWrapper<UserWordProgress>()
                        .eq(UserWordProgress::getUserId, userId)
                        .ge(UserWordProgress::getLastReviewTime, todayStart)
                        .lt(UserWordProgress::getLastReviewTime, todayEnd));
        
        // 今日掌握单词数
        long todayMasteredWords = userWordProgressMapper.selectCount(
                new LambdaQueryWrapper<UserWordProgress>()
                        .eq(UserWordProgress::getUserId, userId)
                        .ge(UserWordProgress::getLastReviewTime, todayStart)
                        .lt(UserWordProgress::getLastReviewTime, todayEnd)
                        .eq(UserWordProgress::getStatus, 2));
        
        stats.put("date", LocalDateTime.now().toLocalDate().toString());
        stats.put("learnTime", todayLearnTime);
        stats.put("hours", Math.round(todayLearnTime / 60.0));
        stats.put("completedLessons", todayCompletedLessons);
        stats.put("learnedWords", todayLearnedWords);
        stats.put("masteredWords", todayMasteredWords);
        
        return stats;
    }

    @Override
    public Map<String, Object> getWeeklyStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7).toLocalDate().atStartOfDay();
        
        // 本周学习时长
        long weekLearnTime = userLessonProgressMapper.selectList(
                new LambdaQueryWrapper<UserLessonProgress>()
                        .eq(UserLessonProgress::getUserId, userId)
                        .ge(UserLessonProgress::getLastLearnTime, weekStart))
                .stream()
                .mapToInt(UserLessonProgress::getLearnTime)
                .sum();
        
        // 本周完成课时数
        long weekCompletedLessons = userLessonProgressMapper.selectCount(
                new LambdaQueryWrapper<UserLessonProgress>()
                        .eq(UserLessonProgress::getUserId, userId)
                        .ge(UserLessonProgress::getLastLearnTime, weekStart)
                        .eq(UserLessonProgress::getIsCompleted, 1));
        
        // 本周学习单词数
        long weekLearnedWords = userWordProgressMapper.selectCount(
                new LambdaQueryWrapper<UserWordProgress>()
                        .eq(UserWordProgress::getUserId, userId)
                        .ge(UserWordProgress::getLastReviewTime, weekStart));
        
        // 每日学习数据（最近7天）
        List<Map<String, Object>> dailyData = new ArrayList<>();
        String[] dayLabels = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (int i = 6; i >= 0; i--) {
            LocalDateTime dayStart = LocalDateTime.now().minusDays(i).toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);
            
            long dayLearnTime = userLessonProgressMapper.selectList(
                    new LambdaQueryWrapper<UserLessonProgress>()
                            .eq(UserLessonProgress::getUserId, userId)
                            .ge(UserLessonProgress::getLastLearnTime, dayStart)
                            .lt(UserLessonProgress::getLastLearnTime, dayEnd))
                    .stream()
                    .mapToInt(UserLessonProgress::getLearnTime)
                    .sum();
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("label", dayLabels[dayStart.getDayOfWeek().getValue() - 1]);
            dayData.put("hours", Math.round(dayLearnTime / 60.0));
            dayData.put("minutes", dayLearnTime);
            dayData.put("date", dayStart.toLocalDate().toString());
            dailyData.add(dayData);
        }
        
        stats.put("weekLearnTime", weekLearnTime);
        stats.put("weekHours", Math.round(weekLearnTime / 60.0));
        stats.put("weekCompletedLessons", weekCompletedLessons);
        stats.put("weekLearnedWords", weekLearnedWords);
        stats.put("dailyData", dailyData);
        
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
        
        // 获取用户已学课程的难度等级分布
        Map<Integer, Integer> levelDistribution = new HashMap<>();
        for (UserCourseProgress progress : userProgresses) {
            Course course = courseMapper.selectById(progress.getCourseId());
            if (course != null && course.getLevel() != null) {
                levelDistribution.merge(course.getLevel(), 1, Integer::sum);
            }
        }
        
        // 推荐策略：优先推荐用户最常学习等级的课程，然后是相邻等级
        int preferredLevel = levelDistribution.isEmpty() ? 1 : 
                levelDistribution.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(1);
        
        // 获取所有可用课程
        List<Course> availableCourses = courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
                .notIn(!enrolledCourseIds.isEmpty(), Course::getId, enrolledCourseIds));
        
        // 按推荐优先级排序：先按与偏好等级的距离排序，再按创建时间排序
        List<Course> sortedCourses = availableCourses.stream()
                .sorted((c1, c2) -> {
                    int diff1 = c1.getLevel() != null ? Math.abs(c1.getLevel() - preferredLevel) : 10;
                    int diff2 = c2.getLevel() != null ? Math.abs(c2.getLevel() - preferredLevel) : 10;
                    if (diff1 != diff2) {
                        return diff1 - diff2;
                    }
                    return c2.getCreatedAt().compareTo(c1.getCreatedAt());
                })
                .limit(5)
                .collect(Collectors.toList());
        
        return sortedCourses.stream()
                .map(course -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", course.getId());
                    map.put("title", course.getTitle());
                    map.put("level", course.getLevel());
                    map.put("coverImage", course.getCoverImage());
                    map.put("price", course.getPrice());
                    map.put("isFree", course.getIsFree());
                    // 添加推荐理由
                    int levelDiff = course.getLevel() != null ? Math.abs(course.getLevel() - preferredLevel) : 10;
                    if (levelDiff == 0) {
                        map.put("recommendReason", "适合您的学习水平");
                    } else if (levelDiff <= 1) {
                        map.put("recommendReason", "难度相近，推荐尝试");
                    } else {
                        map.put("recommendReason", "热门新课程");
                    }
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getWordsForReviewRecommendation(Long userId) {
        List<UserWordProgress> reviewProgresses = userWordProgressMapper.selectList(
                new LambdaQueryWrapper<UserWordProgress>()
                        .eq(UserWordProgress::getUserId, userId)
                        .eq(UserWordProgress::getStatus, 1)
                        .lt(UserWordProgress::getNextReviewTime, LocalDateTime.now())
                        .orderByAsc(UserWordProgress::getNextReviewTime)
                        .last("LIMIT 10"));
        
        return reviewProgresses.stream()
                .map(progress -> {
                    Word word = wordMapper.selectById(progress.getWordId());
                    if (word != null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", word.getId());
                        map.put("word", word.getWord());
                        map.put("pronunciation", word.getPhonetic());
                        map.put("meaning", word.getMeaning());
                        map.put("example", word.getExample());
                        map.put("correctCount", progress.getCorrectCount());
                        map.put("wrongCount", progress.getWrongCount());
                        map.put("lastReviewTime", progress.getLastReviewTime());
                        map.put("nextReviewTime", progress.getNextReviewTime());
                        return map;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public int getReviewWordsCount(Long userId) {
        Long count = userWordProgressMapper.selectCount(new LambdaQueryWrapper<UserWordProgress>()
                .eq(UserWordProgress::getUserId, userId)
                .eq(UserWordProgress::getStatus, 1)
                .lt(UserWordProgress::getNextReviewTime, LocalDateTime.now()));
        return count != null ? count.intValue() : 0;
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
