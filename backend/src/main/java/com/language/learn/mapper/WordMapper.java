
package com.language.learn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.language.learn.entity.Word;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WordMapper extends BaseMapper<Word> {
}
