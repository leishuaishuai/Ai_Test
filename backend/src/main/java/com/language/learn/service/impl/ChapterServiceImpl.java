
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.Chapter;
import com.language.learn.entity.Course;
import com.language.learn.mapper.ChapterMapper;
import com.language.learn.service.ChapterService;
import com.language.learn.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    private final CourseService courseService;

    @Override
    public List<Chapter> getChaptersByCourse(Long courseId) {
        return list(new LambdaQueryWrapper<Chapter>()
                .eq(Chapter::getCourseId, courseId)
                .eq(Chapter::getStatus, 1)
                .orderByAsc(Chapter::getSortOrder));
    }

    @Override
    @Transactional
    public Chapter createChapter(Long courseId, Chapter chapter) {
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        chapter.setCourseId(courseId);
        chapter.setStatus(1);
        save(chapter);
        
        course.setTotalChapters(course.getTotalChapters() + 1);
        courseService.updateById(course);
        
        log.info("创建章节成功: {}", chapter.getTitle());
        return chapter;
    }

    @Override
    @Transactional
    public Chapter updateChapter(Long id, Chapter chapter) {
        Chapter existing = getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("章节不存在");
        }
        
        existing.setTitle(chapter.getTitle());
        existing.setDescription(chapter.getDescription());
        existing.setSortOrder(chapter.getSortOrder());
        existing.setDuration(chapter.getDuration());
        existing.setStatus(chapter.getStatus());
        
        updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public void deleteChapter(Long id) {
        Chapter chapter = getById(id);
        if (chapter == null) {
            throw new IllegalArgumentException("章节不存在");
        }
        
        Course course = courseService.getById(chapter.getCourseId());
        if (course != null) {
            course.setTotalChapters(Math.max(0, course.getTotalChapters() - 1));
            courseService.updateById(course);
        }
        
        removeById(id);
    }
}
