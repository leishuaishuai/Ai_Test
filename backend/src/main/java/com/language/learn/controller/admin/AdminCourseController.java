
package com.language.learn.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.language.learn.entity.Chapter;
import com.language.learn.entity.Course;
import com.language.learn.entity.Lesson;
import com.language.learn.service.ChapterService;
import com.language.learn.service.CourseService;
import com.language.learn.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController {

    private final CourseService courseService;
    private final ChapterService chapterService;
    private final LessonService lessonService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listCourses(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long languageId,
            @RequestParam(required = false) Integer status) {
        
        Page<Course> pageResult = courseService.page(new Page<>(page, size),
                new LambdaQueryWrapper<Course>()
                        .eq(languageId != null, Course::getLanguageId, languageId)
                        .eq(status != null, Course::getStatus, status)
                        .orderByDesc(Course::getCreatedAt));
        
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", Map.of(
                        "records", pageResult.getRecords(),
                        "total", pageResult.getTotal(),
                        "current", pageResult.getCurrent(),
                        "size", pageResult.getSize()
                )
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseDetail(@PathVariable Long id) {
        Course course = courseService.getById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<Chapter> chapters = chapterService.getChaptersByCourse(id);
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonService.getLessonsByChapter(chapter.getId());
            chapter.setDescription(String.valueOf(lessons.size()));
        }
        
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", Map.of(
                        "course", course,
                        "chapters", chapters
                )
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Course course) {
        Course created = courseService.createCourse(course);
        return ResponseEntity.ok(Map.of("code", 200, "message", "创建成功", "data", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        Course updated = courseService.updateCourse(id, course);
        return ResponseEntity.ok(Map.of("code", 200, "message", "更新成功", "data", updated));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateCourseStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        courseService.updateCourseStatus(id, request.get("status"));
        return ResponseEntity.ok(Map.of("code", 200, "message", "状态更新成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(Map.of("code", 200, "message", "删除成功"));
    }

    @PostMapping("/{courseId}/chapters")
    public ResponseEntity<Map<String, Object>> createChapter(@PathVariable Long courseId, @RequestBody Chapter chapter) {
        Chapter created = chapterService.createChapter(courseId, chapter);
        return ResponseEntity.ok(Map.of("code", 200, "message", "创建章节成功", "data", created));
    }

    @PutMapping("/chapters/{chapterId}")
    public ResponseEntity<Map<String, Object>> updateChapter(@PathVariable Long chapterId, @RequestBody Chapter chapter) {
        Chapter updated = chapterService.updateChapter(chapterId, chapter);
        return ResponseEntity.ok(Map.of("code", 200, "message", "更新章节成功", "data", updated));
    }

    @DeleteMapping("/chapters/{chapterId}")
    public ResponseEntity<Map<String, Object>> deleteChapter(@PathVariable Long chapterId) {
        chapterService.deleteChapter(chapterId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "删除章节成功"));
    }

    @PostMapping("/chapters/{chapterId}/lessons")
    public ResponseEntity<Map<String, Object>> createLesson(@PathVariable Long chapterId, @RequestBody Lesson lesson) {
        Lesson created = lessonService.createLesson(chapterId, lesson);
        return ResponseEntity.ok(Map.of("code", 200, "message", "创建课时成功", "data", created));
    }

    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<Map<String, Object>> updateLesson(@PathVariable Long lessonId, @RequestBody Lesson lesson) {
        Lesson updated = lessonService.updateLesson(lessonId, lesson);
        return ResponseEntity.ok(Map.of("code", 200, "message", "更新课时成功", "data", updated));
    }

    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<Map<String, Object>> deleteLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "删除课时成功"));
    }
}
