package com.media.service.impl;

import com.media.entity.Admin;
import com.media.entity.Content;
import com.media.model.AdminDashboardResponse;
import com.media.model.AdminResponse;
import com.media.repository.AdminRepository;
import com.media.repository.ContentRepository;
import com.media.repository.UserRepository;
import com.media.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminResponse login(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(password, admin.getPassword()) && admin.getIsActive()) {
                updateLastLogin(admin.getId());
                return AdminResponse.fromAdmin(admin);
            }
        }
        throw new RuntimeException("用户名或密码错误");
    }

    @Override
    public AdminResponse getAdminById(Long id) {
        Optional<Admin> admin = adminRepository.findById(id);
        return admin.map(AdminResponse::fromAdmin)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
    }

    @Override
    public Page<AdminResponse> getAllAdmins(Pageable pageable) {
        return adminRepository.findAll(pageable)
                .map(AdminResponse::fromAdmin);
    }

    @Override
    public AdminResponse createAdmin(Admin admin) {
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setIsActive(true);
        Admin savedAdmin = adminRepository.save(admin);
        return AdminResponse.fromAdmin(savedAdmin);
    }

    @Override
    public AdminResponse updateAdmin(Long id, Admin admin) {
        Optional<Admin> existingAdmin = adminRepository.findById(id);
        if (!existingAdmin.isPresent()) {
            throw new RuntimeException("管理员不存在");
        }
        
        Admin adminToUpdate = existingAdmin.get();
        adminToUpdate.setRealName(admin.getRealName());
        adminToUpdate.setPhone(admin.getPhone());
        adminToUpdate.setAvatarUrl(admin.getAvatarUrl());
        adminToUpdate.setRole(admin.getRole());
        adminToUpdate.setIsActive(admin.getIsActive());
        
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            adminToUpdate.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        
        Admin savedAdmin = adminRepository.save(adminToUpdate);
        return AdminResponse.fromAdmin(savedAdmin);
    }

    @Override
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new RuntimeException("管理员不存在");
        }
        adminRepository.deleteById(id);
    }

    @Override
    public void updateLastLogin(Long adminId) {
        Optional<Admin> admin = adminRepository.findById(adminId);
        if (admin.isPresent()) {
            Admin adminToUpdate = admin.get();
            adminToUpdate.setLastLogin(LocalDateTime.now());
            adminRepository.save(adminToUpdate);
        }
    }

    @Override
    public AdminDashboardResponse getDashboardData() {
        AdminDashboardResponse response = new AdminDashboardResponse();
        
        // 统计数据
        AdminDashboardResponse.Statistics stats = new AdminDashboardResponse.Statistics();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalContents(contentRepository.count());
        stats.setPendingContents(contentRepository.countByIsPublishedFalse());
        stats.setPublishedContents(contentRepository.countByIsPublishedTrue());
        stats.setTodayRegistrations(userRepository.countByCreatedAtAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)));
        stats.setTodayViews(contentRepository.sumViewCountToday(LocalDateTime.now()));
        response.setStatistics(stats);
        
        // 最近内容
        List<Content> recentContents = contentRepository.findTop10ByOrderByCreatedAtDesc();
        List<AdminDashboardResponse.RecentContent> recentContentList = recentContents.stream()
                .map(this::convertToRecentContent)
                .collect(Collectors.toList());
        response.setRecentContents(recentContentList);
        
        // 最近用户
        List<AdminDashboardResponse.RecentUser> recentUsers = userRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToRecentUser)
                .collect(Collectors.toList());
        response.setRecentUsers(recentUsers);
        
        // 内容状态分布
        Map<String, Integer> contentStatusDistribution = new HashMap<>();
        contentStatusDistribution.put("已发布", contentRepository.countByIsPublishedTrue().intValue());
        contentStatusDistribution.put("待审核", contentRepository.countByIsPublishedFalse().intValue());
        response.setContentStatusDistribution(contentStatusDistribution);
        
        // 用户注册趋势（简化版）
        Map<String, Integer> userRegistrationTrend = new HashMap<>();
        userRegistrationTrend.put("今日", userRepository.countByCreatedAtAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)).intValue());
        userRegistrationTrend.put("本周", userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(7)).intValue());
        userRegistrationTrend.put("本月", userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(30)).intValue());
        response.setUserRegistrationTrend(userRegistrationTrend);
        
        return response;
    }
    
    @Override
    public long getAdminCount() {
        return adminRepository.count();
    }
    
    @Override
    public AdminResponse createDefaultAdmin() {
        // 检查是否已存在默认管理员
        if (adminRepository.existsByUsername("admin")) {
            throw new RuntimeException("默认管理员已存在");
        }
        
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setEmail("admin@example.com");
        admin.setRealName("系统管理员");
        admin.setRole(Admin.AdminRole.SUPER_ADMIN);
        admin.setIsActive(true);
        
        return createAdmin(admin);
    }
    
    private AdminDashboardResponse.RecentContent convertToRecentContent(Content content) {
        AdminDashboardResponse.RecentContent recentContent = new AdminDashboardResponse.RecentContent();
        recentContent.setId(content.getId());
        recentContent.setTitle(content.getTitle());
        recentContent.setStatus(content.getIsPublished() ? "已发布" : "待审核");
        recentContent.setAuthor("用户" + content.getCreatedBy());
        recentContent.setCreatedAt(content.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        recentContent.setViewCount(content.getViewCount());
        return recentContent;
    }
    
    private AdminDashboardResponse.RecentUser convertToRecentUser(com.media.model.User user) {
        AdminDashboardResponse.RecentUser recentUser = new AdminDashboardResponse.RecentUser();
        recentUser.setId(user.getId());
        recentUser.setUsername(user.getUsername());
        recentUser.setEmail(user.getEmail());
        recentUser.setRegisteredAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        recentUser.setContentCount(contentRepository.countByCreatedBy(user.getId()).intValue());
        return recentUser;
    }
} 