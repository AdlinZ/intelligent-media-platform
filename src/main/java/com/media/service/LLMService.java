package com.media.service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LLMService 
{    
  @Value("${llm.api.url}")    
  private String apiUrl;    
  @Value("${llm.api.auth-token}")   
  private String authToken;    
  @Value("${llm.api.model:default-model}")    
  private String model;    
  @Value("${llm.api.max-tokens:200}")    
  private int maxTokens;    
  @Value("${llm.api.response-path:choices.0.text}")    
  private String responsePath;    
  private final RestTemplate restTemplate = new RestTemplate();    
  public String[] generateTopics(String keyword, String difficulty, String language) 
  {        
    // 构建通用请求参数        
    Map<String, Object> requestBody = new HashMap<>();        
    requestBody.put("model", model);        
    // 替换prompt为messages数组
    List<Map<String, String>> userMessage = new ArrayList<>();
    Map<String, String> response1 = new HashMap<>();
    response1.put("role", "user");
    response1.put("content", String.format("请为'%s'主题生成3个%s难度的内容选题，用%s语言，只需返回选题标题，用逗号分隔", keyword, difficulty, language));
    userMessage.add(response1);
    // 修正参数名称：message -> messages
    requestBody.put("messages", userMessage);
    
    // 添加缺失的请求参数
    requestBody.put("stream", false);
    requestBody.put("enable_thinking", true);
    requestBody.put("min_p", 0.05);
    requestBody.put("temperature", 0.7);
    requestBody.put("top_p", 0.7);
    requestBody.put("top_k", 50);
    requestBody.put("frequency_penalty", 0.5);
    // 添加JSON响应格式要求
    Map<String, String> responseFormat = new HashMap<>();
    // 添加缺失的必填参数
    requestBody.put("n", 1);
    requestBody.put("thinking_budget", 4096);
    // 删除空的response_format参数
    // requestBody.put("response_format", new HashMap<>());
    requestBody.put("max_tokens", maxTokens);        
    // 构建通用请求头        
    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();        
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);        
    // 旧代码：headers.set("Authorization", "Bearer " + authToken);
    headers.set("Authorization", "Bearer " +authToken); // 移除Bearer前缀
    headers.set("Content-Type", "application/json");
    // 发送通用API请求
    try {            
      String response = restTemplate.postForObject(apiUrl,new org.springframework.http.HttpEntity<Map<String, Object>>(requestBody, headers),                String.class            );           
       // 解析响应（支持配置化路径）
      System.out.println();
      System.out.println(response);
       JSONObject jsonResponse = JSON.parseObject(response);
       String content = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
       return content.split("，");        }
       catch (Exception e) {            throw new RuntimeException("LLM API调用失败: " + e.getMessage());        }    }
}

