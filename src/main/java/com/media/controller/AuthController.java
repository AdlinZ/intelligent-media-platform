package com.media.controller;

import com.media.model.User;
import com.media.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;  // 添加Valid注解的导入
import com.media.model.RegisterRequest;  // 添加RegisterRequest类的导入
import com.media.model.JwtResponse;       // 添加JwtResponse导入
import com.media.model.MessageResponse;   // 添加MessageResponse导入
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

// 在类顶部添加必要的导入
import com.media.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

// 在类中添加依赖注入
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // 添加用户仓库依赖注入
    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        try {
            String token = authService.authenticateUser(
                    loginRequest.get("username"),
                    loginRequest.get("password")
            );
    
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("tokenType", "Bearer");
    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "登录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = authService.registerUser(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
            // 注册成功后自动登录生成令牌
            try {
                String token = authService.authenticateUser(registerRequest.getUsername(), registerRequest.getPassword());
                return ResponseEntity.ok(new JwtResponse(token, user.getUsername(), user.getEmail()));
            } catch (Exception e) {
                // 自动登录失败，返回注册成功但自动登录失败的提示
                Map<String, Object> result = new HashMap<>();
                result.put("message", "注册成功，但自动登录失败，请手动登录。");
                result.put("username", user.getUsername());
                result.put("email", user.getEmail());
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("joinDate", user.getCreatedAt());
        userInfo.put("avatarUrl", user.getAvatarUrl());
        // 返回完整用户资料信息   
        return ResponseEntity.ok(userInfo);
    }
}