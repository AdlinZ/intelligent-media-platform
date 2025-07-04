package com.media.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "templates")
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String platformType; // wechat, douyin, xiaohongshu, etc.
    private String contentFormat; // 存储模板字符串，使用{{variable}}作为占位符
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}