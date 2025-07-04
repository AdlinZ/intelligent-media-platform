package com.media.model;

import java.util.List;
import java.util.Map;

public class DashboardResponse {
    private Statistics statistics;
    private TrafficData traffic;
    private Map<String, Object> contentAnalysis;
    private Map<String, Object> userEngagement;

    public static class Statistics {
        private long totalReads;
        private double readsTrend;
        private double avgLikesRate;
        private double likesTrend;
        private long shares;
        private double sharesTrend;
        private long comments;
        private double commentsTrend;
        private long totalContent;
        private double contentGrowth;

        // Getters and Setters
        public long getTotalReads() { return totalReads; }
        public void setTotalReads(long totalReads) { this.totalReads = totalReads; }

        public double getReadsTrend() { return readsTrend; }
        public void setReadsTrend(double readsTrend) { this.readsTrend = readsTrend; }

        public double getAvgLikesRate() { return avgLikesRate; }
        public void setAvgLikesRate(double avgLikesRate) { this.avgLikesRate = avgLikesRate; }

        public double getLikesTrend() { return likesTrend; }
        public void setLikesTrend(double likesTrend) { this.likesTrend = likesTrend; }

        public long getShares() { return shares; }
        public void setShares(long shares) { this.shares = shares; }

        public double getSharesTrend() { return sharesTrend; }
        public void setSharesTrend(double sharesTrend) { this.sharesTrend = sharesTrend; }

        public long getComments() { return comments; }
        public void setComments(long comments) { this.comments = comments; }

        public double getCommentsTrend() { return commentsTrend; }
        public void setCommentsTrend(double commentsTrend) { this.commentsTrend = commentsTrend; }

        public long getTotalContent() { return totalContent; }
        public void setTotalContent(long totalContent) { this.totalContent = totalContent; }

        public double getContentGrowth() { return contentGrowth; }
        public void setContentGrowth(double contentGrowth) { this.contentGrowth = contentGrowth; }
    }

    public static class TrafficData {
        private List<String> dates;
        private List<Long> reads;
        private List<Long> likes;
        private List<Long> shares;
        private List<Long> comments;

        // Getters and Setters
        public List<String> getDates() { return dates; }
        public void setDates(List<String> dates) { this.dates = dates; }

        public List<Long> getReads() { return reads; }
        public void setReads(List<Long> reads) { this.reads = reads; }

        public List<Long> getLikes() { return likes; }
        public void setLikes(List<Long> likes) { this.likes = likes; }

        public List<Long> getShares() { return shares; }
        public void setShares(List<Long> shares) { this.shares = shares; }

        public List<Long> getComments() { return comments; }
        public void setComments(List<Long> comments) { this.comments = comments; }
    }

    // Getters and Setters
    public Statistics getStatistics() { return statistics; }
    public void setStatistics(Statistics statistics) { this.statistics = statistics; }

    public TrafficData getTraffic() { return traffic; }
    public void setTraffic(TrafficData traffic) { this.traffic = traffic; }

    public Map<String, Object> getContentAnalysis() { return contentAnalysis; }
    public void setContentAnalysis(Map<String, Object> contentAnalysis) { this.contentAnalysis = contentAnalysis; }

    public Map<String, Object> getUserEngagement() { return userEngagement; }
    public void setUserEngagement(Map<String, Object> userEngagement) { this.userEngagement = userEngagement; }
} 