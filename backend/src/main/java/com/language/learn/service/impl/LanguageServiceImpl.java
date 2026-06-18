
package com.language.learn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.language.learn.entity.Language;
import com.language.learn.mapper.LanguageMapper;
import com.language.learn.service.LanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LanguageServiceImpl extends ServiceImpl<LanguageMapper, Language> implements LanguageService {

    private static final Logger log = LoggerFactory.getLogger(LanguageServiceImpl.class);

    @Override
    public List<Language> getAllLanguages() {
        return list(new LambdaQueryWrapper<Language>()
                .eq(Language::getStatus, 1)
                .orderByAsc(Language::getSortOrder));
    }

    @Override
    public Language getLanguageByCode(String code) {
        return getOne(new LambdaQueryWrapper<Language>()
                .eq(Language::getCode, code)
                .eq(Language::getStatus, 1));
    }

    @Override
    @Transactional
    public Language createLanguage(Language language) {
        if (existsByCode(language.getCode())) {
            throw new IllegalArgumentException("语言代码已存在");
        }
        save(language);
        log.info("创建语言成功: {}", language.getName());
        return language;
    }

    @Override
    @Transactional
    public Language updateLanguage(Long id, Language language) {
        Language existing = getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("语言不存在");
        }
        
        if (!existing.getCode().equals(language.getCode()) && existsByCode(language.getCode())) {
            throw new IllegalArgumentException("语言代码已存在");
        }
        
        existing.setName(language.getName());
        existing.setIcon(language.getIcon());
        existing.setDescription(language.getDescription());
        existing.setSortOrder(language.getSortOrder());
        existing.setStatus(language.getStatus());
        
        updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public void deleteLanguage(Long id) {
        if (getById(id) == null) {
            throw new IllegalArgumentException("语言不存在");
        }
        removeById(id);
    }

    private boolean existsByCode(String code) {
        return count(new LambdaQueryWrapper<Language>().eq(Language::getCode, code)) > 0;
    }
}
