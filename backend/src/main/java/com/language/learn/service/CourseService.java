
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.dto.response.CourseDetailResponse;
import com.language.learn.entity.Course;

import java.util.List;

public interface CourseService extends IService<Course> {

    List<Course> getCoursesByLanguageAndLevel(Long languageId, Integer level);

    Course getCourseById(Long id);

    CourseDetailResponse getCourseDetailById(Long id);

    Course createCourse(Course course);

    Course updateCourse(Long id, Course course);

    void deleteCourse(Long id);

    void updateCourseStatus(Long id, Integer status);
}
