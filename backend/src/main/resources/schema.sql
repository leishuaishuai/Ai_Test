
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
    `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `language` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '语言ID',
    `code` VARCHAR(10) NOT NULL UNIQUE COMMENT '语言代码：en, ja, ko',
    `name` VARCHAR(50) NOT NULL COMMENT '语言名称',
    `icon` VARCHAR(255) COMMENT '语言图标',
    `description` VARCHAR(500) COMMENT '语言描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='语言表';

CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    `language_id` BIGINT NOT NULL COMMENT '语言ID',
    `title` VARCHAR(100) NOT NULL COMMENT '课程标题',
    `description` TEXT COMMENT '课程描述',
    `level` TINYINT NOT NULL COMMENT '难度等级：1-入门，2-初级，3-中级，4-高级',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `total_chapters` INT DEFAULT 0 COMMENT '章节总数',
    `duration` INT COMMENT '课程时长（分钟）',
    `price` DECIMAL(10,2) DEFAULT 0 COMMENT '价格',
    `is_free` TINYINT DEFAULT 0 COMMENT '是否免费：0-付费，1-免费',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    FOREIGN KEY (`language_id`) REFERENCES `language`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

CREATE TABLE IF NOT EXISTS `chapter` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '章节ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `title` VARCHAR(100) NOT NULL COMMENT '章节标题',
    `description` TEXT COMMENT '章节描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `duration` INT COMMENT '时长（分钟）',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节表';

CREATE TABLE IF NOT EXISTS `lesson` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课时ID',
    `chapter_id` BIGINT NOT NULL COMMENT '章节ID',
    `title` VARCHAR(100) NOT NULL COMMENT '课时标题',
    `type` TINYINT NOT NULL COMMENT '类型：1-单词，2-语法，3-口语，4-听力',
    `content` TEXT COMMENT '内容',
    `audio_url` VARCHAR(255) COMMENT '音频URL',
    `video_url` VARCHAR(255) COMMENT '视频URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `duration` INT COMMENT '时长（分钟）',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    FOREIGN KEY (`chapter_id`) REFERENCES `chapter`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课时表';

CREATE TABLE IF NOT EXISTS `word` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '单词ID',
    `language_id` BIGINT NOT NULL COMMENT '语言ID',
    `word` VARCHAR(100) NOT NULL COMMENT '单词',
    `phonetic` VARCHAR(50) COMMENT '音标',
    `meaning` VARCHAR(500) COMMENT '中文释义',
    `example` VARCHAR(500) COMMENT '例句',
    `example_translation` VARCHAR(500) COMMENT '例句翻译',
    `level` TINYINT DEFAULT 1 COMMENT '难度等级',
    `audio_url` VARCHAR(255) COMMENT '发音URL',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    FOREIGN KEY (`language_id`) REFERENCES `language`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单词表';

CREATE TABLE IF NOT EXISTS `user_course_progress` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '进度ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `completed_chapters` INT DEFAULT 0 COMMENT '已完成章节数',
    `completed_lessons` INT DEFAULT 0 COMMENT '已完成课时数',
    `progress_percent` DECIMAL(5,2) DEFAULT 0 COMMENT '进度百分比',
    `last_learn_time` DATETIME COMMENT '最后学习时间',
    `is_completed` TINYINT DEFAULT 0 COMMENT '是否完成：0-未完成，1-已完成',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    UNIQUE KEY `uk_user_course` (`user_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户课程进度表';

CREATE TABLE IF NOT EXISTS `user_lesson_progress` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '进度ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `lesson_id` BIGINT NOT NULL COMMENT '课时ID',
    `is_completed` TINYINT DEFAULT 0 COMMENT '是否完成：0-未完成，1-已完成',
    `score` INT DEFAULT 0 COMMENT '得分',
    `learn_time` INT DEFAULT 0 COMMENT '学习时长（秒）',
    `last_learn_time` DATETIME COMMENT '最后学习时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`lesson_id`) REFERENCES `lesson`(`id`),
    UNIQUE KEY `uk_user_lesson` (`user_id`, `lesson_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户课时进度表';

CREATE TABLE IF NOT EXISTS `user_word_progress` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '进度ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `word_id` BIGINT NOT NULL COMMENT '单词ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-未学习，1-学习中，2-已掌握',
    `correct_count` INT DEFAULT 0 COMMENT '正确次数',
    `wrong_count` INT DEFAULT 0 COMMENT '错误次数',
    `last_review_time` DATETIME COMMENT '最后复习时间',
    `next_review_time` DATETIME COMMENT '下次复习时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`word_id`) REFERENCES `word`(`id`),
    UNIQUE KEY `uk_user_word` (`user_id`, `word_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户单词进度表';

CREATE TABLE IF NOT EXISTS `achievement` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成就ID',
    `title` VARCHAR(100) NOT NULL COMMENT '成就标题',
    `description` VARCHAR(500) COMMENT '成就描述',
    `icon` VARCHAR(255) COMMENT '成就图标',
    `type` TINYINT NOT NULL COMMENT '类型：1-学习时长，2-课程完成，3-单词掌握，4-连续打卡',
    `condition_value` INT NOT NULL COMMENT '条件值',
    `condition_unit` VARCHAR(20) COMMENT '条件单位',
    `points` INT DEFAULT 0 COMMENT '积分奖励',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成就表';

CREATE TABLE IF NOT EXISTS `user_achievement` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `achievement_id` BIGINT NOT NULL COMMENT '成就ID',
    `is_unlocked` TINYINT DEFAULT 0 COMMENT '是否解锁：0-未解锁，1-已解锁',
    `unlocked_at` DATETIME COMMENT '解锁时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`achievement_id`) REFERENCES `achievement`(`id`),
    UNIQUE KEY `uk_user_achievement` (`user_id`, `achievement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户成就表';

CREATE TABLE IF NOT EXISTS `user_points` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '积分记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `points` INT NOT NULL COMMENT '积分数（正为加，负为减）',
    `type` TINYINT NOT NULL COMMENT '类型：1-学习奖励，2-成就奖励，3-签到奖励',
    `description` VARCHAR(200) COMMENT '描述',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分记录表';

CREATE TABLE IF NOT EXISTS `user_daily_sign` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '签到记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `sign_date` DATE NOT NULL COMMENT '签到日期',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `sign_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户每日签到表';

CREATE TABLE IF NOT EXISTS `post` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '帖子标题',
    `content` TEXT COMMENT '帖子内容',
    `image_urls` VARCHAR(1000) COMMENT '图片URL，逗号分隔',
    `language_id` BIGINT COMMENT '关联语言ID',
    `view_count` INT DEFAULT 0 COMMENT '浏览量',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT DEFAULT 0 COMMENT '评论数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`language_id`) REFERENCES `language`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    FOREIGN KEY (`post_id`) REFERENCES `post`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

CREATE TABLE IF NOT EXISTS `post_like` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`post_id`) REFERENCES `post`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_post_user` (`post_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';

INSERT INTO `language` (`code`, `name`, `icon`, `description`, `sort_order`) VALUES
('en', '英语', '🇺🇸', '世界通用语言', 1),
('ja', '日语', '🇯🇵', '日本官方语言', 2),
('ko', '韩语', '🇰🇷', '韩国官方语言', 3);

INSERT INTO `achievement` (`title`, `description`, `icon`, `type`, `condition_value`, `condition_unit`, `points`) VALUES
('初学者', '完成第一节课', '🏆', 2, 1, '节', 10),
('坚持不懈', '连续打卡7天', '🔥', 4, 7, '天', 50),
('词汇达人', '掌握100个单词', '📚', 3, 100, '个', 100),
('学习狂人', '累计学习10小时', '⏰', 1, 600, '分钟', 80),
('课程大师', '完成一门完整课程', '🎓', 2, 1, '门', 200);
