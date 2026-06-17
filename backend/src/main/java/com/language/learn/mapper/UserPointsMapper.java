
package com.language.learn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.language.learn.entity.UserPoints;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserPointsMapper extends BaseMapper<UserPoints> {

    @Select("SELECT COALESCE(SUM(points), 0) FROM user_points WHERE user_id = #{userId}")
    Integer selectSumByUserId(Long userId);
}
