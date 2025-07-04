package com.media.controller;

import com.media.entity.Admin;
import com.media.model.AdminDashboardResponse;
import com.media.model.AdminLoginRequest;
import com.media.model.AdminResponse;
import com.media.model.MessageResponse;
import com.media.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest loginRequest) {
        try {
            AdminResponse admin = adminService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        try {
            AdminDashboardResponse dashboard = adminService.getDashboardData();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/admins")
    public ResponseEntity<?> getAllAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AdminResponse> admins = adminService.getAllAdmins(pageable);
            return ResponseEntity.ok(admins);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/admins/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        try {
            AdminResponse admin = adminService.getAdminById(id);
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/admins")
    public ResponseEntity<?> createAdmin(@RequestBody Admin admin) {
        try {
            AdminResponse createdAdmin = adminService.createAdmin(admin);
            return ResponseEntity.ok(createdAdmin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/admins/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        try {
            AdminResponse updatedAdmin = adminService.updateAdmin(id, admin);
            return ResponseEntity.ok(updatedAdmin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/admins/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            return ResponseEntity.ok(new MessageResponse("管理员删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 测试接口 - 检查管理员状态
    @GetMapping("/test/status")
    public ResponseEntity<?> getAdminStatus() {
        try {
            long adminCount = adminService.getAdminCount();
            Map<String, Object> response = new HashMap<>();
            response.put("adminCount", adminCount);
            response.put("message", adminCount > 0 ? "数据库中有管理员数据" : "数据库中没有管理员数据");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // 测试接口 - 创建默认管理员
    @PostMapping("/test/create-default")
    public ResponseEntity<?> createDefaultAdmin() {
        try {
            AdminResponse admin = adminService.createDefaultAdmin();
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
} 