
package com.language.learn.controller;

import com.language.learn.entity.Course;
import com.language.learn.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCourses(
            @RequestParam(required = false) Long languageId) {
        List<Course> courses = courseService.getCoursesByLanguage(languageId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", course));
    }
}
