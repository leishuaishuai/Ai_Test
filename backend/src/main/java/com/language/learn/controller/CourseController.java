
package com.language.learn.controller;

import com.language.learn.dto.response.CourseDetailResponse;
import com.language.learn.entity.Course;
import com.language.learn.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCourses(
            @RequestParam(required = false) Long languageId,
            @RequestParam(required = false) Integer level) {
        List<Course> courses = courseService.getCoursesByLanguageAndLevel(languageId, level);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable Long id) {
        CourseDetailResponse course = courseService.getCourseDetailById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", course));
    }
}
