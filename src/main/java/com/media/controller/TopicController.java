package com.media.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;

import com.media.service.LLMService;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/topic")
public class TopicController {

    @Autowired
    private LLMService llmService;
    @GetMapping("/generate")
    public ResponseEntity<?> generateTopics(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "medium") String difficulty,
            @RequestParam(defaultValue = "zh-CN") String language) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 调用 LLM 生成选题
            String[] topics = llmService.generateTopics(keyword, difficulty, language);
            response.put("topics", topics);
            response.put("success", true);
        } catch (Exception e) {
            // LLM异常时返回默认选题
            response.put("topics", new String[]{
                "AI赋能内容创作的五大趋势",
                "自媒体涨粉的实用技巧",
                "短视频内容策划与制作指南"
            });
            response.put("success", false);
            response.put("message", "选题生成失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hot")
    public ResponseEntity<?> getHotTopics() {
        // 实现热门选题获取逻辑
        Map<String, Object> response = new HashMap<>();
        response.put("topics", new String[]{
            "人工智能在内容创作中的应用",
            "自媒体运营涨粉技巧分享",
            "短视频内容策划与制作指南"
        });
        return ResponseEntity.ok(response);
    }
}