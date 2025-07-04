package com.media.controller;

import com.media.entity.Content;
import com.media.model.MessageResponse;
import com.media.repository.ContentRepository;
import com.media.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/content")
@CrossOrigin(origins = "*")
public class ContentManagementController {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    public ResponseEntity<?> getContentList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Content> contents;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                contents = contentRepository.findByTitleContainingAndPublished(keyword, pageable);
            } else if (status != null && !status.trim().isEmpty()) {
                boolean isPublished = "published".equals(status);
                if (isPublished) {
                    contents = contentRepository.findAllPublished(pageable);
                } else {
                    contents = contentRepository.findAll(pageable);
                }
            } else {
                contents = contentRepository.findAll(pageable);
            }
            
            return ResponseEntity.ok(contents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContentById(@PathVariable Long id) {
        try {
            Optional<Content> content = contentRepository.findById(id);
            if (content.isPresent()) {
                return ResponseEntity.ok(content.get());
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("内容不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveContent(@PathVariable Long id) {
        try {
            Optional<Content> contentOpt = contentRepository.findById(id);
            if (contentOpt.isPresent()) {
                Content content = contentOpt.get();
                content.setIsPublished(true);
                content.setUpdatedAt(LocalDateTime.now());
                contentRepository.save(content);
                return ResponseEntity.ok(new MessageResponse("内容审核通过"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("内容不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectContent(@PathVariable Long id) {
        try {
            Optional<Content> contentOpt = contentRepository.findById(id);
            if (contentOpt.isPresent()) {
                Content content = contentOpt.get();
                content.setIsPublished(false);
                content.setUpdatedAt(LocalDateTime.now());
                contentRepository.save(content);
                return ResponseEntity.ok(new MessageResponse("内容已拒绝"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("内容不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContent(@PathVariable Long id) {
        try {
            if (contentRepository.existsById(id)) {
                contentRepository.deleteById(id);
                return ResponseEntity.ok(new MessageResponse("内容删除成功"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("内容不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getContentStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalContents", contentRepository.count());
            statistics.put("publishedContents", contentRepository.countByIsPublishedTrue());
            statistics.put("pendingContents", contentRepository.countByIsPublishedFalse());
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
} 