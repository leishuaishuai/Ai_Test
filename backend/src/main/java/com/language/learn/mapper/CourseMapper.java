
package com.language.learn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.language.learn.entity.Course;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
