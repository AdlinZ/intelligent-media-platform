package com.media.controller;

import com.media.model.DashboardResponse;
import com.media.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardResponse> getDashboardStats() {
        try {
            DashboardResponse response = dashboardService.getDashboardStats();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/traffic")
    public ResponseEntity<Map<String, Object>> getTrafficData(
            @RequestParam(defaultValue = "7") int days) {
        try {
            Map<String, Object> data = dashboardService.getTrafficData(days);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/content-analysis")
    public ResponseEntity<Map<String, Object>> getContentAnalysis() {
        try {
            Map<String, Object> data = dashboardService.getContentAnalysis();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user-engagement")
    public ResponseEntity<Map<String, Object>> getUserEngagement() {
        try {
            Map<String, Object> data = dashboardService.getUserEngagement();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 