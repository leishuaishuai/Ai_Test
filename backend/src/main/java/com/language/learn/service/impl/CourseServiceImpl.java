
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.dto.response.ChapterWithLessonsResponse;
import com.language.learn.dto.response.CourseDetailResponse;
import com.language.learn.dto.response.LessonResponse;
import com.language.learn.entity.Chapter;
import com.language.learn.entity.Course;
import com.language.learn.entity.Language;
import com.language.learn.entity.Lesson;
import com.language.learn.mapper.CourseMapper;
import com.language.learn.service.ChapterService;
import com.language.learn.service.CourseService;
import com.language.learn.service.LanguageService;
import com.language.learn.service.LessonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final LanguageService languageService;
    private final ChapterService chapterService;
    private final LessonService lessonService;

    public CourseServiceImpl(LanguageService languageService, @Lazy ChapterService chapterService, LessonService lessonService) {
        this.languageService = languageService;
        this.chapterService = chapterService;
        this.lessonService = lessonService;
    }

    @Override
    public List<Course> getCoursesByLanguageAndLevel(Long languageId, Integer level) {
        return list(new LambdaQueryWrapper<Course>()
                .eq(languageId != null, Course::getLanguageId, languageId)
                .eq(level != null, Course::getLevel, level)
                .eq(Course::getStatus, 1)
                .orderByAsc(Course::getSortOrder));
    }

    @Override
    public Course getCourseById(Long id) {
        return getOne(new LambdaQueryWrapper<Course>()
                .eq(Course::getId, id)
                .eq(Course::getStatus, 1));
    }

    @Override
    public CourseDetailResponse getCourseDetailById(Long id) {
        Course course = getCourseById(id);
        if (course == null) {
            return null;
        }

        CourseDetailResponse response = new CourseDetailResponse();
        response.setId(course.getId());
        response.setLanguageId(course.getLanguageId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setLevel(course.getLevel());
        response.setCoverImage(course.getCoverImage());
        response.setTotalChapters(course.getTotalChapters());
        response.setDuration(course.getDuration());
        response.setPrice(course.getPrice());
        response.setIsFree(course.getIsFree());
        response.setStatus(course.getStatus());
        response.setSortOrder(course.getSortOrder());
        response.setCreatedAt(course.getCreatedAt());
        response.setUpdatedAt(course.getUpdatedAt());

        // 获取章节列表
        List<Chapter> chapters = chapterService.getChaptersByCourse(id);
        List<ChapterWithLessonsResponse> chapterResponses = new ArrayList<>();

        for (Chapter chapter : chapters) {
            ChapterWithLessonsResponse chapterResponse = new ChapterWithLessonsResponse();
            chapterResponse.setId(chapter.getId());
            chapterResponse.setCourseId(chapter.getCourseId());
            chapterResponse.setTitle(chapter.getTitle());
            chapterResponse.setDescription(chapter.getDescription());
            chapterResponse.setSortOrder(chapter.getSortOrder());
            chapterResponse.setDuration(chapter.getDuration());
            chapterResponse.setStatus(chapter.getStatus());
            chapterResponse.setCreatedAt(chapter.getCreatedAt());
            chapterResponse.setUpdatedAt(chapter.getUpdatedAt());

            // 获取课时列表
            List<Lesson> lessons = lessonService.getLessonsByChapter(chapter.getId());
            List<LessonResponse> lessonResponses = lessons.stream().map(lesson -> {
                LessonResponse lessonResponse = new LessonResponse();
                lessonResponse.setId(lesson.getId());
                lessonResponse.setChapterId(lesson.getChapterId());
                lessonResponse.setTitle(lesson.getTitle());
                lessonResponse.setType(lesson.getType());
                lessonResponse.setContent(lesson.getContent());
                lessonResponse.setAudioUrl(lesson.getAudioUrl());
                lessonResponse.setVideoUrl(lesson.getVideoUrl());
                lessonResponse.setSortOrder(lesson.getSortOrder());
                lessonResponse.setDuration(lesson.getDuration());
                lessonResponse.setStatus(lesson.getStatus());
                lessonResponse.setCreatedAt(lesson.getCreatedAt());
                lessonResponse.setUpdatedAt(lesson.getUpdatedAt());
                return lessonResponse;
            }).collect(Collectors.toList());

            chapterResponse.setLessons(lessonResponses);
            chapterResponses.add(chapterResponse);
        }

        response.setChapters(chapterResponses);
        return response;
    }

    @Override
    @Transactional
    public Course createCourse(Course course) {
        Language language = languageService.getById(course.getLanguageId());
        if (language == null) {
            throw new IllegalArgumentException("语言不存在");
        }
        
        course.setTotalChapters(0);
        course.setStatus(1);
        save(course);
        log.info("创建课程成功: {}", course.getTitle());
        return course;
    }

    @Override
    @Transactional
    public Course updateCourse(Long id, Course course) {
        Course existing = getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        if (course.getLanguageId() != null) {
            Language language = languageService.getById(course.getLanguageId());
            if (language == null) {
                throw new IllegalArgumentException("语言不存在");
            }
            existing.setLanguageId(course.getLanguageId());
        }
        
        existing.setTitle(course.getTitle());
        existing.setDescription(course.getDescription());
        existing.setLevel(course.getLevel());
        existing.setCoverImage(course.getCoverImage());
        existing.setDuration(course.getDuration());
        existing.setPrice(course.getPrice());
        existing.setIsFree(course.getIsFree());
        existing.setSortOrder(course.getSortOrder());
        
        updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        if (getById(id) == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional
    public void updateCourseStatus(Long id, Integer status) {
        Course course = getById(id);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        course.setStatus(status);
        updateById(course);
        log.info("课程状态更新: {} -> {}", course.getTitle(), status == 1 ? "上架" : "下架");
    }
}
