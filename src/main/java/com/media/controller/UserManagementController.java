package com.media.controller;

import com.media.model.MessageResponse;
import com.media.model.User;
import com.media.repository.ContentRepository;
import com.media.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class UserManagementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    public ResponseEntity<?> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 这里可以添加按用户名或邮箱搜索的逻辑
                users = userRepository.findAll(pageable);
            } else {
                users = userRepository.findAll(pageable);
            }
            
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("用户不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userUpdate) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setEmail(userUpdate.getEmail());
                user.setPhone(userUpdate.getPhone());
                user.setAvatarUrl(userUpdate.getAvatarUrl());
                user.setRoles(userUpdate.getRoles());
                
                if (userUpdate.getPassword() != null && !userUpdate.getPassword().isEmpty()) {
                    user.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
                }
                
                User savedUser = userRepository.save(user);
                return ResponseEntity.ok(savedUser);
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("用户不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (userRepository.existsById(id)) {
                // 先删除用户的所有内容
                contentRepository.deleteByCreatedBy(id);
                // 再删除用户
                userRepository.deleteById(id);
                return ResponseEntity.ok(new MessageResponse("用户删除成功"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("用户不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}/contents")
    public ResponseEntity<?> getUserContents(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            // 这里需要添加按用户ID查询内容的方法
            return ResponseEntity.ok(new MessageResponse("功能开发中"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getUserStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalUsers", userRepository.count());
            statistics.put("todayRegistrations", userRepository.countByCreatedAtAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)));
            statistics.put("weekRegistrations", userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(7)));
            statistics.put("monthRegistrations", userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(30)));
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
} 