package com.media.model;

import com.media.entity.Admin;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminResponse {
    private Long id;
    private String username;
    private String email;
    private String realName;
    private String phone;
    private String avatarUrl;
    private Admin.AdminRole role;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdminResponse fromAdmin(Admin admin) {
        AdminResponse response = new AdminResponse();
        response.setId(admin.getId());
        response.setUsername(admin.getUsername());
        response.setEmail(admin.getEmail());
        response.setRealName(admin.getRealName());
        response.setPhone(admin.getPhone());
        response.setAvatarUrl(admin.getAvatarUrl());
        response.setRole(admin.getRole());
        response.setIsActive(admin.getIsActive());
        response.setLastLogin(admin.getLastLogin());
        response.setCreatedAt(admin.getCreatedAt());
        response.setUpdatedAt(admin.getUpdatedAt());
        return response;
    }
} 