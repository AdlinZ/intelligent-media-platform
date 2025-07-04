package com.media.service;

import com.media.model.ArticleResponse;
import com.media.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    private final ContentRepository contentRepository;

    @Autowired
    public ArticleService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public ArticleResponse getArticles(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<?> articlesPage;

        if (keyword != null && !keyword.isEmpty()) {
            articlesPage = contentRepository.findByTitleContainingAndPublished(keyword, pageable);
        } else {
            articlesPage = contentRepository.findAllPublished(pageable);
        }

        ArticleResponse response = new ArticleResponse();
        response.setRecords(articlesPage.getContent());
        response.setTotal(articlesPage.getTotalElements());
        // 添加调试日志
        System.out.println("查询条件: keyword=" + keyword + ", page=" + page + ", size=" + size);
        System.out.println("查询结果: 共" + articlesPage.getTotalElements() + "篇文章");
        // 添加空数据处理
        if (articlesPage.getContent().isEmpty()) {
            response.setMessage("暂无符合条件的文章");
        }
        return response;
    }

    public ArticleResponse getArticleById(Long id) {
        try {
            com.media.entity.Content content = contentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("文章不存在"));
            
            ArticleResponse response = new ArticleResponse();
            response.setRecords(java.util.Arrays.asList(content));
            response.setTotal(1L);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("获取文章详情失败: " + e.getMessage());
        }
    }
}