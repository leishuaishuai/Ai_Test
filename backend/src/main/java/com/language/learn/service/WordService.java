
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.entity.Word;

import java.util.List;

public interface WordService extends IService<Word> {

    List<Word> getWordsByLanguage(Long languageId);

    List<Word> getWordsForReview(Long userId);

    Word createWord(Word word);

    Word updateWord(Long id, Word word);

    void deleteWord(Long id);
}
