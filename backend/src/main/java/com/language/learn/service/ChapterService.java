
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.entity.Chapter;

import java.util.List;

public interface ChapterService extends IService<Chapter> {

    List<Chapter> getChaptersByCourse(Long courseId);

    Chapter createChapter(Long courseId, Chapter chapter);

    Chapter updateChapter(Long id, Chapter chapter);

    void deleteChapter(Long id);
}
