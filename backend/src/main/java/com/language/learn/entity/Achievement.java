
package com.language.learn.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("achievement")
public class Achievement {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private String icon;

    private Integer type;

    private Integer conditionValue;

    private String conditionUnit;

    private Integer points;

    private Integer sortOrder;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
