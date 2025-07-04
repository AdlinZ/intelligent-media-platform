package com.media.service.impl;

import com.media.model.DashboardResponse;
import com.media.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Override
    public DashboardResponse getDashboardStats() {
        DashboardResponse response = new DashboardResponse();
        
        // 统计数据
        DashboardResponse.Statistics stats = new DashboardResponse.Statistics();
        stats.setTotalReads(12580);
        stats.setReadsTrend(12.5);
        stats.setAvgLikesRate(3.8);
        stats.setLikesTrend(2.3);
        stats.setShares(845);
        stats.setSharesTrend(5.7);
        stats.setComments(326);
        stats.setCommentsTrend(-1.2);
        stats.setTotalContent(156);
        stats.setContentGrowth(8.5);
        
        response.setStatistics(stats);
        
        // 流量数据
        DashboardResponse.TrafficData traffic = new DashboardResponse.TrafficData();
        List<String> dates = new ArrayList<>();
        List<Long> reads = Arrays.asList(3200L, 4500L, 4880L, 5200L, 4800L, 5600L, 6100L);
        List<Long> likes = Arrays.asList(120L, 180L, 165L, 200L, 185L, 220L, 240L);
        List<Long> shares = Arrays.asList(230L, 310L, 305L, 350L, 320L, 380L, 420L);
        List<Long> comments = Arrays.asList(45L, 68L, 52L, 75L, 60L, 85L, 95L);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        for (int i = 6; i >= 0; i--) {
            dates.add(LocalDate.now().minusDays(i).format(formatter));
        }
        
        traffic.setDates(dates);
        traffic.setReads(reads);
        traffic.setLikes(likes);
        traffic.setShares(shares);
        traffic.setComments(comments);
        
        response.setTraffic(traffic);
        
        // 内容分析
        Map<String, Object> contentAnalysis = new HashMap<>();
        contentAnalysis.put("categories", Arrays.asList("科技", "娱乐", "体育", "财经", "教育"));
        contentAnalysis.put("performance", Arrays.asList(85, 72, 68, 91, 76));
        contentAnalysis.put("engagement", Arrays.asList(4.2, 3.8, 3.5, 4.8, 3.9));
        
        response.setContentAnalysis(contentAnalysis);
        
        // 用户参与度
        Map<String, Object> userEngagement = new HashMap<>();
        userEngagement.put("labels", Arrays.asList("点赞", "评论", "转发", "收藏"));
        userEngagement.put("data", Arrays.asList(470, 326, 845, 520));
        userEngagement.put("colors", Arrays.asList("#409EFF", "#67C23A", "#E6A23C", "#F56C6C"));
        
        response.setUserEngagement(userEngagement);
        
        return response;
    }

    @Override
    public Map<String, Object> getTrafficData(int days) {
        Map<String, Object> data = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Long> reads = new ArrayList<>();
        List<Long> likes = new ArrayList<>();
        List<Long> shares = new ArrayList<>();
        List<Long> comments = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        Random random = new Random();
        
        for (int i = days - 1; i >= 0; i--) {
            dates.add(LocalDate.now().minusDays(i).format(formatter));
            reads.add(3000L + random.nextInt(4000));
            likes.add(100L + random.nextInt(200));
            shares.add(200L + random.nextInt(300));
            comments.add(40L + random.nextInt(80));
        }
        
        data.put("dates", dates);
        data.put("reads", reads);
        data.put("likes", likes);
        data.put("shares", shares);
        data.put("comments", comments);
        
        return data;
    }

    @Override
    public Map<String, Object> getContentAnalysis() {
        Map<String, Object> data = new HashMap<>();
        data.put("categories", Arrays.asList("科技", "娱乐", "体育", "财经", "教育", "健康", "旅游"));
        data.put("performance", Arrays.asList(85, 72, 68, 91, 76, 82, 79));
        data.put("engagement", Arrays.asList(4.2, 3.8, 3.5, 4.8, 3.9, 4.1, 3.7));
        data.put("publishRate", Arrays.asList(15, 12, 8, 20, 10, 14, 11));
        
        return data;
    }

    @Override
    public Map<String, Object> getUserEngagement() {
        Map<String, Object> data = new HashMap<>();
        data.put("labels", Arrays.asList("点赞", "评论", "转发", "收藏", "关注"));
        data.put("data", Arrays.asList(470, 326, 845, 520, 1280));
        data.put("colors", Arrays.asList("#409EFF", "#67C23A", "#E6A23C", "#F56C6C", "#909399"));
        data.put("trends", Arrays.asList(12.5, 8.3, 15.7, 6.2, 22.1));
        
        return data;
    }
} 