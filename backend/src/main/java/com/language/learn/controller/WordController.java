
package com.language.learn.controller;

import com.language.learn.entity.Word;
import com.language.learn.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getWords(@RequestParam Long languageId) {
        List<Word> words = wordService.getWordsByLanguage(languageId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", words));
    }

    @GetMapping("/review")
    public ResponseEntity<Map<String, Object>> getWordsForReview() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        List<Word> words = wordService.getWordsForReview(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", words));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getWordById(@PathVariable Long id) {
        Word word = wordService.getById(id);
        if (word == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", word));
    }
}
