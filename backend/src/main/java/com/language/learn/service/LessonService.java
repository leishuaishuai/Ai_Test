
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.entity.Lesson;

import java.util.List;

public interface LessonService extends IService<Lesson> {

    List<Lesson> getLessonsByChapter(Long chapterId);

    Lesson getLessonById(Long id);

    Lesson createLesson(Long chapterId, Lesson lesson);

    Lesson updateLesson(Long id, Lesson lesson);

    void deleteLesson(Long id);
}
