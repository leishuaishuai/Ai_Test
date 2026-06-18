
package com.language.learn.controller;

import com.language.learn.entity.Chapter;
import com.language.learn.service.ChapterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getChaptersByCourse(@PathVariable Long courseId) {
        List<Chapter> chapters = chapterService.getChaptersByCourse(courseId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", chapters));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getChapterById(@PathVariable Long id) {
        Chapter chapter = chapterService.getById(id);
        if (chapter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", chapter));
    }
}
