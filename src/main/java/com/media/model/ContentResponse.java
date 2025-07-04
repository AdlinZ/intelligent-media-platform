package com.media.model;

import lombok.Data;

@Data
public class ContentResponse {
    private String title;
    private String content;
    private String warning;
    private long generateTime;
    private Long id;
    private boolean success;
}