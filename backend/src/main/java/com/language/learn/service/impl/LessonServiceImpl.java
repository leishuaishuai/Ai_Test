
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.Chapter;
import com.language.learn.entity.Lesson;
import com.language.learn.mapper.LessonMapper;
import com.language.learn.service.ChapterService;
import com.language.learn.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonServiceImpl extends ServiceImpl<LessonMapper, Lesson> implements LessonService {

    private final ChapterService chapterService;

    @Override
    public List<Lesson> getLessonsByChapter(Long chapterId) {
        return list(new LambdaQueryWrapper<Lesson>()
                .eq(Lesson::getChapterId, chapterId)
                .eq(Lesson::getStatus, 1)
                .orderByAsc(Lesson::getSortOrder));
    }

    @Override
    public Lesson getLessonById(Long id) {
        return getOne(new LambdaQueryWrapper<Lesson>()
                .eq(Lesson::getId, id)
                .eq(Lesson::getStatus, 1));
    }

    @Override
    @Transactional
    public Lesson createLesson(Long chapterId, Lesson lesson) {
        Chapter chapter = chapterService.getById(chapterId);
        if (chapter == null) {
            throw new IllegalArgumentException("章节不存在");
        }
        
        lesson.setChapterId(chapterId);
        lesson.setStatus(1);
        save(lesson);
        
        log.info("创建课时成功: {}", lesson.getTitle());
        return lesson;
    }

    @Override
    @Transactional
    public Lesson updateLesson(Long id, Lesson lesson) {
        Lesson existing = getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("课时不存在");
        }
        
        existing.setTitle(lesson.getTitle());
        existing.setType(lesson.getType());
        existing.setContent(lesson.getContent());
        existing.setAudioUrl(lesson.getAudioUrl());
        existing.setVideoUrl(lesson.getVideoUrl());
        existing.setSortOrder(lesson.getSortOrder());
        existing.setDuration(lesson.getDuration());
        existing.setStatus(lesson.getStatus());
        
        updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public void deleteLesson(Long id) {
        if (!existsById(id)) {
            throw new IllegalArgumentException("课时不存在");
        }
        removeById(id);
    }
}
