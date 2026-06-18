-- =====================================================
-- 语言学习平台示例数据初始化脚本
-- =====================================================

-- -----------------------------------------------------
-- 语言数据（3种语言）
-- -----------------------------------------------------
INSERT INTO `language` (`id`, `name`, `code`, `icon`, `status`) VALUES
(1, '英语', 'en', '🇺🇸', 1),
(2, '日语', 'ja', '🇯🇵', 1),
(3, '韩语', 'ko', '🇰🇷', 1);

-- -----------------------------------------------------
-- 课程数据（每种语言2个课程：入门级和初级）
-- -----------------------------------------------------
-- 英语课程
INSERT INTO `course` (`id`, `language_id`, `title`, `description`, `level`, `cover_image`, `total_chapters`, `duration`, `price`, `is_free`, `status`, `sort_order`) VALUES
(1, 1, '英语入门课程', '适合零基础学习者，从字母和基础发音开始，循序渐进掌握英语基础', 1, '/images/course/en-basic.jpg', 2, 120, 0.00, 1, 1, 1),
(2, 1, '英语初级课程', '适合有一定基础的学习者，学习日常对话和基础语法', 2, '/images/course/en-elementary.jpg', 2, 180, 99.00, 0, 1, 2);

-- 日语课程
INSERT INTO `course` (`id`, `language_id`, `title`, `description`, `level`, `cover_image`, `total_chapters`, `duration`, `price`, `is_free`, `status`, `sort_order`) VALUES
(3, 2, '日语入门课程', '从五十音图开始学习，掌握日语基础发音和书写', 1, '/images/course/ja-basic.jpg', 2, 120, 0.00, 1, 1, 1),
(4, 2, '日语初级课程', '学习日常会话和基础语法，为日语学习打下坚实基础', 2, '/images/course/ja-elementary.jpg', 2, 180, 99.00, 0, 1, 2);

-- 韩语课程
INSERT INTO `course` (`id`, `language_id`, `title`, `description`, `level`, `cover_image`, `total_chapters`, `duration`, `price`, `is_free`, `status`, `sort_order`) VALUES
(5, 3, '韩语入门课程', '从韩语字母开始，学习基础发音和简单词汇', 1, '/images/course/ko-basic.jpg', 2, 120, 0.00, 1, 1, 1),
(6, 3, '韩语初级课程', '学习日常对话和基础语法，了解韩国文化', 2, '/images/course/ko-elementary.jpg', 2, 180, 99.00, 0, 1, 2);

-- -----------------------------------------------------
-- 章节数据（每个课程2个章节）
-- -----------------------------------------------------
-- 英语课程章节
INSERT INTO `chapter` (`id`, `course_id`, `title`, `description`, `sort_order`, `status`) VALUES
(1, 1, '字母与发音', '学习英语26个字母及基础发音规则', 1, 1),
(2, 1, '基础词汇', '学习最常用的英语基础词汇', 2, 1),
(3, 2, '日常对话', '学习日常生活中的常用对话', 1, 1),
(4, 2, '基础语法', '学习英语基础语法结构', 2, 1);

-- 日语课程章节
INSERT INTO `chapter` (`id`, `course_id`, `title`, `description`, `sort_order`, `status`) VALUES
(5, 3, '五十音图', '学习日语基础发音系统', 1, 1),
(6, 3, '基础词汇', '学习日语常用基础词汇', 2, 1),
(7, 4, '日常会话', '学习日常生活中的日语对话', 1, 1),
(8, 4, '基础语法', '学习日语基础语法结构', 2, 1);

-- 韩语课程章节
INSERT INTO `chapter` (`id`, `course_id`, `title`, `description`, `sort_order`, `status`) VALUES
(9, 5, '韩语字母', '学习韩语字母系统及发音', 1, 1),
(10, 5, '基础词汇', '学习韩语常用基础词汇', 2, 1),
(11, 6, '日常对话', '学习日常生活中的韩语对话', 1, 1),
(12, 6, '基础语法', '学习韩语基础语法结构', 2, 1);

-- -----------------------------------------------------
-- 课时数据（每个章节2个课时，不同类型）
-- -----------------------------------------------------
-- 英语课程课时
INSERT INTO `lesson` (`id`, `chapter_id`, `title`, `type`, `content`, `audio_url`, `video_url`, `sort_order`, `duration`, `status`) VALUES
(1, 1, '字母A-G学习', 1, '学习字母A到G的发音和书写', '/audio/en/letters-a-g.mp3', '/video/en/letters-a-g.mp4', 1, 15, 1),
(2, 1, '字母发音规则', 2, '学习英语字母的基本发音规则和拼读方法', '/audio/en/phonics.mp3', '/video/en/phonics.mp4', 2, 20, 1),
(3, 2, '基础单词学习', 1, '学习hello, thank, please等基础单词', '/audio/en/basic-words.mp3', NULL, 1, 15, 1),
(4, 2, '单词口语练习', 3, '练习基础单词的口语表达', '/audio/en/speaking-practice.mp3', '/video/en/speaking.mp4', 2, 10, 1),
(5, 3, '问候对话', 3, '学习问候语和简单对话', '/audio/en/greetings.mp3', '/video/en/greetings.mp4', 1, 15, 1),
(6, 3, '对话听力练习', 4, '练习日常对话的听力理解', '/audio/en/listening.mp3', NULL, 2, 20, 1),
(7, 4, '句子结构', 2, '学习英语基本句子结构', NULL, '/video/en/sentence-structure.mp4', 1, 25, 1),
(8, 4, '语法口语应用', 3, '将语法知识应用到口语中', '/audio/en/grammar-speaking.mp3', '/video/en/grammar-speaking.mp4', 2, 15, 1);

-- 日语课程课时
INSERT INTO `lesson` (`id`, `chapter_id`, `title`, `type`, `content`, `audio_url`, `video_url`, `sort_order`, `duration`, `status`) VALUES
(9, 5, '平假名学习', 1, '学习日语平假名的基本发音', '/audio/ja/hiragana.mp3', '/video/ja/hiragana.mp4', 1, 20, 1),
(10, 5, '片假名学习', 1, '学习日语片假名的基本发音', '/audio/ja/katakana.mp3', '/video/ja/katakana.mp4', 2, 20, 1),
(11, 6, '基础词汇', 1, '学习こんにちは、ありがとう等基础词汇', '/audio/ja/basic-words.mp3', NULL, 1, 15, 1),
(12, 6, '词汇听力', 4, '练习基础词汇的听力', '/audio/ja/listening.mp3', NULL, 2, 15, 1),
(13, 7, '日常问候', 3, '学习日语日常问候语', '/audio/ja/greetings.mp3', '/video/ja/greetings.mp4', 1, 15, 1),
(14, 7, '对话练习', 3, '练习日常对话场景', '/audio/ja/dialogue.mp3', '/video/ja/dialogue.mp4', 2, 20, 1),
(15, 8, '基本句型', 2, '学习日语基本句型结构', NULL, '/video/ja/sentence.mp4', 1, 25, 1),
(16, 8, '语法听力', 4, '通过听力练习巩固语法', '/audio/ja/grammar-listening.mp3', NULL, 2, 20, 1);

-- 韩语课程课时
INSERT INTO `lesson` (`id`, `chapter_id`, `title`, `type`, `content`, `audio_url`, `video_url`, `sort_order`, `duration`, `status`) VALUES
(17, 9, '韩语字母发音', 1, '学习韩语字母的基本发音', '/audio/ko/letters.mp3', '/video/ko/letters.mp4', 1, 20, 1),
(18, 9, '字母组合', 2, '学习韩语字母的组合规则', '/audio/ko/combo.mp3', '/video/ko/combo.mp4', 2, 15, 1),
(19, 10, '基础词汇', 1, '学习안녕하세요、감사합니다等基础词汇', '/audio/ko/basic-words.mp3', NULL, 1, 15, 1),
(20, 10, '词汇口语', 3, '练习基础词汇的口语表达', '/audio/ko/speaking.mp3', '/video/ko/speaking.mp4', 2, 10, 1),
(21, 11, '日常问候', 3, '学习韩语日常问候语', '/audio/ko/greetings.mp3', '/video/ko/greetings.mp4', 1, 15, 1),
(22, 11, '对话听力', 4, '练习日常对话的听力理解', '/audio/ko/listening.mp3', NULL, 2, 20, 1),
(23, 12, '基本句型', 2, '学习韩语基本句型结构', NULL, '/video/ko/sentence.mp4', 1, 25, 1),
(24, 12, '语法应用', 3, '将语法应用到口语中', '/audio/ko/grammar-speaking.mp3', '/video/ko/grammar-speaking.mp4', 2, 15, 1);

-- -----------------------------------------------------
-- 单词数据（每种语言20个单词）
-- -----------------------------------------------------
-- 英语单词（20个）
INSERT INTO `word` (`language_id`, `word`, `pronunciation`, `meaning`, `example`, `example_translation`, `context`, `audio_url`, `level`) VALUES
(1, 'hello', '/həˈloʊ/', '你好，问候语', 'Hello, how are you?', '你好，你怎么样？', '日常问候用语', '/audio/en/hello.mp3', 1),
(1, 'thank', '/θæŋk/', '感谢，谢谢', 'Thank you for your help.', '谢谢你的帮助。', '表达感谢', '/audio/en/thank.mp3', 1),
(1, 'please', '/pliːz/', '请，请求', 'Please help me.', '请帮帮我。', '礼貌请求', '/audio/en/please.mp3', 1),
(1, 'good', '/ɡʊd/', '好的，好的', 'This is a good book.', '这是一本好书。', '形容品质', '/audio/en/good.mp3', 1),
(1, 'morning', '/ˈmɔːrnɪŋ/', '早晨，上午', 'Good morning!', '早上好！', '时间表达', '/audio/en/morning.mp3', 1),
(1, 'afternoon', '/ˈæftərˈnuːn/', '下午', 'Good afternoon!', '下午好！', '时间表达', '/audio/en/afternoon.mp3', 1),
(1, 'evening', '/ˈiːvnɪŋ/', '傍晚，晚上', 'Good evening!', '晚上好！', '时间表达', '/audio/en/evening.mp3', 1),
(1, 'night', '/naɪt/', '夜晚', 'Good night!', '晚安！', '时间表达', '/audio/en/night.mp3', 1),
(1, 'water', '/ˈwɔːtər/', '水', 'I need some water.', '我需要一些水。', '日常生活', '/audio/en/water.mp3', 1),
(1, 'food', '/fuːd/', '食物', 'The food is delicious.', '食物很美味。', '日常生活', '/audio/en/food.mp3', 1),
(1, 'book', '/bʊk/', '书，书籍', 'I am reading a book.', '我正在读一本书。', '学习用品', '/audio/en/book.mp3', 2),
(1, 'school', '/skuːl/', '学校', 'I go to school every day.', '我每天都去学校。', '教育场所', '/audio/en/school.mp3', 2),
(1, 'teacher', '/ˈtiːtʃər/', '老师', 'The teacher is very kind.', '老师很亲切。', '教育人物', '/audio/en/teacher.mp3', 2),
(1, 'student', '/ˈstuːdənt/', '学生', 'I am a student.', '我是一个学生。', '教育人物', '/audio/en/student.mp3', 2),
(1, 'friend', '/frend/', '朋友', 'She is my best friend.', '她是我最好的朋友。', '人际关系', '/audio/en/friend.mp3', 2),
(1, 'family', '/ˈfæməli/', '家庭', 'I love my family.', '我爱我的家庭。', '人际关系', '/audio/en/family.mp3', 2),
(1, 'happy', '/ˈhæpi/', '快乐的，幸福的', 'I am very happy today.', '今天我很开心。', '情感表达', '/audio/en/happy.mp3', 2),
(1, 'beautiful', '/ˈbjʊtɪfʊl/', '美丽的', 'The flower is beautiful.', '花很美丽。', '形容词', '/audio/en/beautiful.mp3', 2),
(1, 'work', '/wɜːrk/', '工作', 'I work at a company.', '我在一家公司工作。', '职业', '/audio/en/work.mp3', 2),
(1, 'time', '/taɪm/', '时间', 'What time is it?', '现在几点了？', '时间概念', '/audio/en/time.mp3', 2);

-- 日语单词（20个）
INSERT INTO `word` (`language_id`, `word`, `pronunciation`, `meaning`, `example`, `example_translation`, `context`, `audio_url`, `level`) VALUES
(2, 'こんにちは', 'konnichiwa', '你好', 'こんにちは、お元気ですか？', '你好，你好吗？', '日常问候', '/audio/ja/konnichiwa.mp3', 1),
(2, 'ありがとう', 'arigatou', '谢谢', 'ありがとうございます。', '非常感谢。', '表达感谢', '/audio/ja/arigatou.mp3', 1),
(2, 'おはよう', 'ohayou', '早上好', 'おはようございます。', '早上好。', '早晨问候', '/audio/ja/ohayou.mp3', 1),
(2, 'こんばんは', 'konbanwa', '晚上好', 'こんばんは、今日はどうでしたか？', '晚上好，今天怎么样？', '晚间问候', '/audio/ja/konbanwa.mp3', 1),
(2, 'さようなら', 'sayounara', '再见', 'さようなら、また会いましょう。', '再见，下次再见。', '告别用语', '/audio/ja/sayounara.mp3', 1),
(2, 'すみません', 'sumimasen', '对不起/不好意思', 'すみません、道を教えてください。', '不好意思，请告诉我路怎么走。', '道歉/请求', '/audio/ja/sumimasen.mp3', 1),
(2, 'はい', 'hai', '是的', 'はい、わかりました。', '是的，我明白了。', '肯定回答', '/audio/ja/hai.mp3', 1),
(2, 'いいえ', 'iie', '不是/不', 'いいえ、そうではありません。', '不，不是那样的。', '否定回答', '/audio/ja/iie.mp3', 1),
(2, '水', 'mizu', '水', '水をください。', '请给我水。', '日常生活', '/audio/ja/mizu.mp3', 1),
(2, '食べ物', 'tabemono', '食物', 'この食べ物は美味しいです。', '这个食物很美味。', '日常生活', '/audio/ja/tabemono.mp3', 1),
(2, '本', 'hon', '书', 'この本を読んでいます。', '我正在读这本书。', '学习用品', '/audio/ja/hon.mp3', 2),
(2, '学校', 'gakkou', '学校', '学校に行きます。', '我去学校。', '教育场所', '/audio/ja/gakkou.mp3', 2),
(2, '先生', 'sensei', '老师', '先生はとても親切です。', '老师很亲切。', '教育人物', '/audio/ja/sensei.mp3', 2),
(2, '学生', 'gakusei', '学生', '私は学生です。', '我是学生。', '教育人物', '/audio/ja/gakusei.mp3', 2),
(2, '友達', 'tomodachi', '朋友', '彼は私の友達です。', '他是我的朋友。', '人际关系', '/audio/ja/tomodachi.mp3', 2),
(2, '家族', 'kazoku', '家庭/家人', '家族を愛しています。', '我爱我的家人。', '人际关系', '/audio/ja/kazoku.mp3', 2),
(2, '幸せ', 'shiawase', '幸福', '私は幸せです。', '我很幸福。', '情感表达', '/audio/ja/shiawase.mp3', 2),
(2, '美しい', 'utsukushii', '美丽的', '花は美しいです。', '花很美丽。', '形容词', '/audio/ja/utsukushii.mp3', 2),
(2, '仕事', 'shigoto', '工作', '仕事に行きます。', '我去工作。', '职业', '/audio/ja/shigoto.mp3', 2),
(2, '時間', 'jikan', '时间', '時間は何時ですか？', '现在几点了？', '时间概念', '/audio/ja/jikan.mp3', 2);

-- 韩语单词（20个）
INSERT INTO `word` (`language_id`, `word`, `pronunciation`, `meaning`, `example`, `example_translation`, `context`, `audio_url`, `level`) VALUES
(3, '안녕하세요', 'annyeonghaseyo', '你好', '안녕하세요, 잘 지내세요?', '你好，你好吗？', '日常问候', '/audio/ko/annyeonghaseyo.mp3', 1),
(3, '감사합니다', 'gamsahamnida', '谢谢', '감사합니다.', '谢谢。', '表达感谢', '/audio/ko/gamsahamnida.mp3', 1),
(3, '안녕', 'annyeong', '你好/再见', '안녕!', '你好/再见！', '非正式问候', '/audio/ko/annyeong.mp3', 1),
(3, '미안합니다', 'mianhamnida', '对不起', '미안합니다.', '对不起。', '道歉', '/audio/ko/mianhamnida.mp3', 1),
(3, '네', 'ne', '是的', '네, 알겠습니다.', '是的，我知道了。', '肯定回答', '/audio/ko/ne.mp3', 1),
(3, '아니요', 'aniyo', '不是', '아니요, 그렇지 않습니다.', '不，不是那样的。', '否定回答', '/audio/ko/aniyo.mp3', 1),
(3, '좋은 아침', 'joeun achim', '早上好', '좋은 아침!', '早上好！', '早晨问候', '/audio/ko/joeun-achim.mp3', 1),
(3, '잘 자요', 'jal jayo', '晚安', '잘 자요.', '晚安。', '睡前问候', '/audio/ko/jal-jayo.mp3', 1),
(3, '물', 'mul', '水', '물을 주세요.', '请给我水。', '日常生活', '/audio/ko/mul.mp3', 1),
(3, '음식', 'eumsik', '食物', '이 음식은 맛있습니다.', '这个食物很美味。', '日常生活', '/audio/ko/eumsik.mp3', 1),
(3, '책', 'chaek', '书', '책을 읽고 있습니다.', '我正在读书。', '学习用品', '/audio/ko/chaek.mp3', 2),
(3, '학교', 'hakgyo', '学校', '학교에 갑니다.', '我去学校。', '教育场所', '/audio/ko/hakgyo.mp3', 2),
(3, '선생님', 'seonsaengnim', '老师', '선생님은 친절합니다.', '老师很亲切。', '教育人物', '/audio/ko/seonsaengnim.mp3', 2),
(3, '학생', 'haksaeng', '学生', '저는 학생입니다.', '我是学生。', '教育人物', '/audio/ko/haksaeng.mp3', 2),
(3, '친구', 'chingu', '朋友', '그는 내 친구입니다.', '他是我的朋友。', '人际关系', '/audio/ko/chingu.mp3', 2),
(3, '가족', 'gajok', '家庭/家人', '가족을 사랑합니다.', '我爱我的家人。', '人际关系', '/audio/ko/gajok.mp3', 2),
(3, '행복', 'haengbok', '幸福', '저는 행복합니다.', '我很幸福。', '情感表达', '/audio/ko/haengbok.mp3', 2),
(3, '아름다운', 'areumdaun', '美丽的', '꽃이 아름다운입니다.', '花很美丽。', '形容词', '/audio/ko/areumdaun.mp3', 2),
(3, '일', 'il', '工作', '일을 합니다.', '我在工作。', '职业', '/audio/ko/il.mp3', 2),
(3, '시간', 'sigan', '时间', '시간이 몇 시입니까?', '现在几点了？', '时间概念', '/audio/ko/sigan.mp3', 2);

-- -----------------------------------------------------
-- 成就数据（5个成就定义）
-- -----------------------------------------------------
INSERT INTO `achievement` (`id`, `name`, `description`, `icon`, `type`, `threshold`, `points`) VALUES
(1, '初学者', '完成第一节课的学习', '🏆', 2, 1, 10),
(2, '坚持不懈', '连续打卡7天', '🔥', 4, 7, 50),
(3, '词汇达人', '掌握100个单词', '📚', 3, 100, 100),
(4, '学习狂人', '累计学习时长达到10小时', '⏰', 1, 600, 80),
(5, '课程大师', '完成一门完整课程', '🎓', 2, 1, 200);

-- -----------------------------------------------------
-- 用户数据（2个测试用户）
-- -----------------------------------------------------
-- 注意：密码使用BCrypt加密，这里是示例加密后的密码
-- 普通用户密码：user123
-- 管理员密码：admin123
INSERT INTO `user` (`id`, `username`, `password`, `email`, `nickname`, `avatar`, `level`, `points`, `role`, `status`) VALUES
(1, 'testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'testuser@example.com', '测试用户', '/avatars/user1.jpg', 1, 0, 0, 1),
(2, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@example.com', '管理员', '/avatars/admin.jpg', 5, 1000, 1, 1);