package com.mediagenius.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/avatars")
public class AvatarController {

    @Value("${app.avatar.upload-path}")
    private String uploadPath;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file, Authentication authentication) {
        // 验证文件是否为空
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("上传文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("只能上传图片文件");
        }

        // 验证文件大小（限制10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件大小不能超过10MB");
        }

        try {
            // 创建上传目录（如果不存在）
            Path uploadDir = Paths.get(uploadPath);
            Files.createDirectories(uploadDir);

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.')) : ".jpg";
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // 保存文件到指定目录
            Path targetLocation = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 构建文件访问URL
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/avatars/")
                    .path(fileName)
                    .toUriString();

            return ResponseEntity.ok(fileDownloadUri);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("上传文件失败: " + ex.getMessage());
        }
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}