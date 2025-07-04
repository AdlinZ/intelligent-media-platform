package com.media.controller;

import com.media.model.ContentRequest;
import com.media.model.ContentResponse;
import com.media.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content") // 添加/api前缀
public class ContentController {

    private final ContentService contentService;

    @Autowired
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ContentResponse> generateContent(@RequestBody ContentRequest request) {
        ContentResponse response = new ContentResponse();
        try {
            // 调用 LLM 生成内容
            ContentResponse llmResponse = contentService.generateContent(request);
            response.setTitle(llmResponse.getTitle());
            response.setContent(llmResponse.getContent());
            response.setSuccess(true);
        } catch (Exception e) {
            // LLM异常时返回模板内容
            response.setTitle("AI内容生成失败");
            response.setContent("很抱歉，AI内容生成失败，请稍后重试。\n错误信息：" + e.getMessage());
            response.setSuccess(false);
            response.setWarning("内容生成失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/convert")
    public ResponseEntity<ContentResponse> convertContent(
            @RequestBody ContentRequest request,
            @RequestParam String platformType) {
        ContentResponse response = contentService.convertContent(request, platformType);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<ContentResponse> saveContent(@RequestBody ContentRequest request) {
        ContentResponse response = new ContentResponse();
        try {
            ContentResponse saveResp = contentService.saveContent(request);
            response.setId(saveResp.getId());
            response.setTitle(saveResp.getTitle());
            response.setContent(saveResp.getContent());
            response.setSuccess(saveResp.isSuccess());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setWarning("内容保存失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/topics")
    public ResponseEntity<String[]> getTopics(@RequestParam String keyword) {
        String[] topics = contentService.getTopicsByKeyword(keyword);
        return ResponseEntity.ok(topics);
    }

    @PostMapping("/test-save")
    public ResponseEntity<ContentResponse> testSaveContent() {
        ContentResponse response = new ContentResponse();
        try {
            ContentRequest testRequest = new ContentRequest();
            testRequest.setTitle("测试内容");
            testRequest.setContent("这是一个测试内容，用于验证保存功能是否正常。");
            testRequest.setKeyword("测试");
            
            ContentResponse saveResp = contentService.saveContent(testRequest);
            response.setId(saveResp.getId());
            response.setTitle(saveResp.getTitle());
            response.setContent(saveResp.getContent());
            response.setSuccess(saveResp.isSuccess());
            response.setWarning("测试保存成功");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setWarning("测试保存失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}