package com.media.model;

import java.util.List;

public class ArticleResponse {
    private List<?> records;
    private long total;
    private String message;

    // Getters and setters
    public List<?> getRecords() { return records; }
    public void setRecords(List<?> records) { this.records = records; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}