package com.media.controller;

import com.media.model.User;
import com.media.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    private static final String UPLOAD_DIR = "src/main/resources/static/avatars/";

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file, Authentication authentication) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("请选择要上传的头像文件");
        }

        try {
            // 创建上传目录（如果不存在）
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // 保存文件到本地
            Files.write(filePath, file.getBytes());

            // 更新用户头像URL
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            String avatarUrl = "/avatars/" + fileName;
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            // 返回更新后的头像URL
            Map<String, String> response = new HashMap<>();
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("上传头像失败: " + e.getMessage());
        }
    }
}