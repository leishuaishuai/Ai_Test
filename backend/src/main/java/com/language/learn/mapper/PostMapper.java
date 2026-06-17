
package com.language.learn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.language.learn.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
