
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.Course;
import com.language.learn.entity.Language;
import com.language.learn.mapper.CourseMapper;
import com.language.learn.service.CourseService;
import com.language.learn.service.LanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final LanguageService languageService;

    @Override
    public List<Course> getCoursesByLanguage(Long languageId) {
        return list(new LambdaQueryWrapper<Course>()
                .eq(languageId != null, Course::getLanguageId, languageId)
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
        if (!existsById(id)) {
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
