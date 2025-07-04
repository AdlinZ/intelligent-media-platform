package com.media.service;

import com.media.model.DashboardResponse;
import java.util.Map;

public interface DashboardService {
    DashboardResponse getDashboardStats();
    Map<String, Object> getTrafficData(int days);
    Map<String, Object> getContentAnalysis();
    Map<String, Object> getUserEngagement();
} 