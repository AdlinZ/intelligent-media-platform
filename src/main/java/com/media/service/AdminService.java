package com.media.service;

import com.media.entity.Admin;
import com.media.model.AdminDashboardResponse;
import com.media.model.AdminResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    AdminResponse login(String username, String password);
    AdminResponse getAdminById(Long id);
    Page<AdminResponse> getAllAdmins(Pageable pageable);
    AdminResponse createAdmin(Admin admin);
    AdminResponse updateAdmin(Long id, Admin admin);
    void deleteAdmin(Long id);
    void updateLastLogin(Long adminId);
    AdminDashboardResponse getDashboardData();
    long getAdminCount();
    AdminResponse createDefaultAdmin();
} 