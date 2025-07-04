package com.media.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AdminDashboardResponse {
    private Statistics statistics;
    private List<RecentContent> recentContents;
    private List<RecentUser> recentUsers;
    private Map<String, Integer> contentStatusDistribution;
    private Map<String, Integer> userRegistrationTrend;

    @Data
    public static class Statistics {
        private Long totalUsers;
        private Long totalContents;
        private Long pendingContents;
        private Long publishedContents;
        private Long todayRegistrations;
        private Long todayViews;
    }

    @Data
    public static class RecentContent {
        private Long id;
        private String title;
        private String status;
        private String author;
        private String createdAt;
        private Integer viewCount;
    }

    @Data
    public static class RecentUser {
        private Long id;
        private String username;
        private String email;
        private String registeredAt;
        private Integer contentCount;
    }
} 