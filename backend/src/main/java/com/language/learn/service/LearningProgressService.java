
package com.language.learn.service;

import com.language.learn.entity.UserCourseProgress;
import com.language.learn.entity.UserLessonProgress;
import com.language.learn.entity.UserWordProgress;

import java.util.List;
import java.util.Map;

public interface LearningProgressService {

    UserCourseProgress getCourseProgress(Long userId, Long courseId);

    List<UserCourseProgress> getUserCourseProgress(Long userId);

    UserCourseProgress updateCourseProgress(Long userId, Long courseId);

    UserLessonProgress getLessonProgress(Long userId, Long lessonId);

    UserLessonProgress updateLessonProgress(Long userId, Long lessonId, Integer score, Integer learnTime);

    UserWordProgress getWordProgress(Long userId, Long wordId);

    UserWordProgress updateWordProgress(Long userId, Long wordId, Boolean isCorrect);

    Map<String, Object> getLearningStatistics(Long userId);

    List<Map<String, Object>> getRecommendedCourses(Long userId);
}
