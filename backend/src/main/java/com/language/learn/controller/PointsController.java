
package com.language.learn.controller;

import com.language.learn.entity.UserPoints;
import com.language.learn.service.UserPointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointsController {

    private final UserPointsService userPointsService;

    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getTotalPoints() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        Integer total = userPointsService.getUserTotalPoints(userId);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", total));
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getPointsHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        List<UserPoints> history = userPointsService.getUserPointsHistory(userId, page, size);
        return ResponseEntity.ok(Map.of("code", 200, "message", "获取成功", "data", history));
    }
}
