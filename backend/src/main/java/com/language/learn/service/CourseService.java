
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.entity.Course;

import java.util.List;

public interface CourseService extends IService<Course> {

    List<Course> getCoursesByLanguage(Long languageId);

    Course getCourseById(Long id);

    Course createCourse(Course course);

    Course updateCourse(Long id, Course course);

    void deleteCourse(Long id);

    void updateCourseStatus(Long id, Integer status);
}
