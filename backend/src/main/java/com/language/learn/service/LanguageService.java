
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.entity.Language;

import java.util.List;

public interface LanguageService extends IService<Language> {

    List<Language> getAllLanguages();

    Language getLanguageByCode(String code);

    Language createLanguage(Language language);

    Language updateLanguage(Long id, Language language);

    void deleteLanguage(Long id);
}
