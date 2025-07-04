package com.media.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/features")
public class FeatureController {

    @GetMapping
    public Map<String, Object> getFeatures() {
        Map<String, Object> response = new HashMap<>();
        response.put("title", "项目功能介绍");
        response.put("description", "探索我们平台的核心功能和特性");

        List<Map<String, Object>> features = Arrays.asList(
            createFeature("用户认证", "安全的注册与登录系统", Arrays.asList("JWT令牌身份验证", "基于角色的权限管理")),
            createFeature("内容管理", "创建、编辑和删除内容", Arrays.asList("内容历史版本追踪", "富文本编辑器支持")),
            createFeature("主题管理", "多主题支持", Arrays.asList("自定义主题设置", "主题预览功能")),
            createFeature("数据分析", "内容统计与分析", Arrays.asList("用户行为追踪", "可视化数据展示"))
        );

        response.put("features", features);
        return response;
    }

    private Map<String, Object> createFeature(String title, String description, List<String> details) {
        Map<String, Object> feature = new HashMap<>();
        feature.put("title", title);
        feature.put("description", description);
        feature.put("details", details);
        return feature;
    }
}