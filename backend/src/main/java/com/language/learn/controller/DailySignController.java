
package com.language.learn.controller;

import com.language.learn.service.DailySignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sign")
@RequiredArgsConstructor
public class DailySignController {

    private final DailySignService dailySignService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sign() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> result = dailySignService.sign(userId);
        return ResponseEntity.ok(Map.of("code", 200, "data", result));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSignStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        boolean isSigned = dailySignService.isSignedToday(userId);
        int continuousDays = dailySignService.getContinuousSignDays(userId);
        int totalDays = dailySignService.getTotalSignDays(userId);
        
        return ResponseEntity.ok(Map.of("code", 200, "data", Map.of(
                "isSigned", isSigned,
                "continuousDays", continuousDays,
                "totalDays", totalDays
        )));
    }
}
