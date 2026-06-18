
package com.language.learn.controller;

import com.language.learn.entity.Lesson;
import com.language.learn.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<Map<String, Object>> getLessonsByChapter(@PathVariable Long chapterId) {
        List<Lesson> lessons = lessonService.getLessonsByChapter(chapterId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", lessons));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLessonById(@PathVariable Long id) {
        Lesson lesson = lessonService.getLessonById(id);
        if (lesson == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", lesson));
    }
}
