
package com.language.learn.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_lesson_progress")
public class UserLessonProgress {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long lessonId;

    private Integer isCompleted;

    private Integer score;

    private Integer learnTime;

    private LocalDateTime lastLearnTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
