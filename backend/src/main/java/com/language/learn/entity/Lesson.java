
package com.language.learn.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lesson")
public class Lesson {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long chapterId;

    private String title;

    private Integer type;

    private String content;

    private String audioUrl;

    private String videoUrl;

    private Integer sortOrder;

    private Integer duration;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
