
package com.language.learn.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_daily_sign")
public class UserDailySign {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate signDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
