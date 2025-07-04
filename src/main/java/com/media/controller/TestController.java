package com.media.controller;

import com.media.entity.Admin;
import com.media.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/admin-check")
    public ResponseEntity<?> checkAdmin() {
        Map<String, Object> result = new HashMap<>();
        
        // 检查admin用户是否存在
        Optional<Admin> adminOpt = adminRepository.findByUsername("admin");
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            result.put("exists", true);
            result.put("username", admin.getUsername());
            result.put("email", admin.getEmail());
            result.put("role", admin.getRole());
            result.put("isActive", admin.getIsActive());
            
            // 测试密码验证
            boolean passwordMatches = passwordEncoder.matches("admin123", admin.getPassword());
            result.put("passwordMatches", passwordMatches);
            result.put("storedPassword", admin.getPassword());
        } else {
            result.put("exists", false);
        }
        
        // 统计管理员总数
        long totalAdmins = adminRepository.count();
        result.put("totalAdmins", totalAdmins);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin() {
        try {
            // 检查是否已存在
            if (adminRepository.existsByUsername("admin")) {
                return ResponseEntity.ok("管理员已存在");
            }
            
            // 创建新管理员
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setRealName("系统管理员");
            admin.setRole(Admin.AdminRole.SUPER_ADMIN);
            admin.setIsActive(true);
            
            Admin savedAdmin = adminRepository.save(admin);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "管理员创建成功");
            result.put("id", savedAdmin.getId());
            result.put("username", savedAdmin.getUsername());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("创建失败: " + e.getMessage());
        }
    }
} 