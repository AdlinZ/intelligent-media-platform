package com.media.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ContentRequest {

    @NotBlank(message = "选题关键词不能为空")
    @Size(max = 50, message = "选题关键词不能超过50个字符")
    private String keyword;

    @Size(max = 100, message = "标题不能超过100个字符")
    private String title;

    private String content;

    private String difficulty = "medium";

    private Integer wordCount = 200;
}