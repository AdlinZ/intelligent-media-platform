package com.media.controller;

import com.media.model.ArticleResponse;
import com.media.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<ArticleResponse> getArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        ArticleResponse response = articleService.getArticles(page, size, keyword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleById(@PathVariable Long id) {
        try {
            ArticleResponse article = articleService.getArticleById(id);
            return ResponseEntity.ok(article);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}