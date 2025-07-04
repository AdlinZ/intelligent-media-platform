package com.media.service;

import com.media.model.ContentRequest;
import com.media.model.ContentResponse;

public interface ContentService {

    /**
     * 根据选题关键词生成内容摘要
     * @param request 包含选题关键词和其他参数的请求对象
     * @return 包含生成内容的响应对象
     */
    ContentResponse generateContent(ContentRequest request);

    /**
     * 将内容转换为指定平台的格式
     * @param request 包含原始内容的请求对象
     * @param platformType 目标平台类型（wechat, douyin, xiaohongshu等）
     * @return 包含转换后内容的响应对象
     */
    ContentResponse convertContent(ContentRequest request, String platformType);

    /**
     * 根据关键词获取热点选题
     * @param keyword 内容领域关键词
     * @return 热点选题数组
     */
    String[] getTopicsByKeyword(String keyword);

    /**
     * 保存内容到数据库
     */
    ContentResponse saveContent(ContentRequest request);
}