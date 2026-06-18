
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.UserWordProgress;
import com.language.learn.entity.Word;
import com.language.learn.mapper.UserWordProgressMapper;
import com.language.learn.mapper.WordMapper;
import com.language.learn.service.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WordServiceImpl extends ServiceImpl<WordMapper, Word> implements WordService {

    private static final Logger log = LoggerFactory.getLogger(WordServiceImpl.class);

    private final UserWordProgressMapper userWordProgressMapper;

    public WordServiceImpl(UserWordProgressMapper userWordProgressMapper) {
        this.userWordProgressMapper = userWordProgressMapper;
    }

    @Override
    public List<Word> getWordsByLanguage(Long languageId) {
        return list(new LambdaQueryWrapper<Word>()
                .eq(Word::getLanguageId, languageId)
                .orderByAsc(Word::getLevel)
                .orderByAsc(Word::getId));
    }

    @Override
    public List<Word> getWordsForReview(Long userId) {
        List<Long> wordIds = userWordProgressMapper.selectList(new LambdaQueryWrapper<UserWordProgress>()
                        .eq(UserWordProgress::getUserId, userId)
                        .eq(UserWordProgress::getStatus, 1)
                        .lt(UserWordProgress::getNextReviewTime, LocalDateTime.now()))
                .stream()
                .map(UserWordProgress::getWordId)
                .toList();
        
        if (wordIds.isEmpty()) {
            return list(new LambdaQueryWrapper<Word>()
                    .orderByAsc(Word::getId)
                    .last("LIMIT 20"));
        }
        
        return list(new LambdaQueryWrapper<Word>()
                .in(Word::getId, wordIds)
                .last("LIMIT 20"));
    }

    @Override
    @Transactional
    public Word createWord(Word word) {
        save(word);
        log.info("创建单词成功: {}", word.getWord());
        return word;
    }

    @Override
    @Transactional
    public Word updateWord(Long id, Word word) {
        Word existing = getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("单词不存在");
        }
        
        existing.setWord(word.getWord());
        existing.setPhonetic(word.getPhonetic());
        existing.setMeaning(word.getMeaning());
        existing.setExample(word.getExample());
        existing.setExampleTranslation(word.getExampleTranslation());
        existing.setLevel(word.getLevel());
        existing.setAudioUrl(word.getAudioUrl());
        
        updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public void deleteWord(Long id) {
        if (getById(id) == null) {
            throw new IllegalArgumentException("单词不存在");
        }
        removeById(id);
    }
}
