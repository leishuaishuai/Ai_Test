
package com.language.learn.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("word")
public class Word {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long languageId;

    private String word;

    private String phonetic;

    private String meaning;

    private String example;

    private String exampleTranslation;

    private Integer level;

    private String audioUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
