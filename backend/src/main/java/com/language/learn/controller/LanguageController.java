
package com.language.learn.controller;

import com.language.learn.entity.Language;
import com.language.learn.service.LanguageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {

    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLanguages() {
        List<Language> languages = languageService.getAllLanguages();
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", languages));
    }

    @GetMapping("/{code}")
    public ResponseEntity<Map<String, Object>> getLanguageByCode(@PathVariable String code) {
        Language language = languageService.getLanguageByCode(code);
        if (language == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", language));
    }
}
