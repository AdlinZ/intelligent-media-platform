package com.media.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.media.entity.Content;
import com.media.model.User;
import com.media.model.ContentRequest;
import com.media.model.ContentResponse;
import com.media.repository.ContentRepository;
import com.media.repository.UserRepository;
import com.media.security.JwtTokenProvider;
import com.media.service.ContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ContentServiceImpl implements ContentService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String llmApiUrl;
    private final String llmApiKey;
    private final boolean localMode;
    private final Map<String, List<String>> topicMap;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;

    // 构造函数注入依赖
    public ContentServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper,
                             @Value("${llm.api.url}") String llmApiUrl,
                             @Value("${llm.api.key}") String llmApiKey,
                             @Value("${llm.local-mode}") boolean localMode,
                             ContentRepository contentRepository,
                             UserRepository userRepository,
                             JwtTokenProvider jwtTokenProvider,
                             HttpServletRequest request) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.llmApiUrl = llmApiUrl;
        this.llmApiKey = llmApiKey;
        this.localMode = localMode;
        this.contentRepository = contentRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.request = request;
        this.topicMap = loadTopicsFromJson();
    }

    @Override
    public ContentResponse generateContent(ContentRequest request) {
        ContentResponse response = new ContentResponse();
        response.setGenerateTime(System.currentTimeMillis());
        String topic = request.getKeyword();
        if (localMode) {
            // 使用本地模板生成内容
            response.setTitle(topic);
            String rawContent = generateContentFromTemplate(request);
            // 将Markdown转换为HTML
            String htmlContent = convertMarkdownToHtml(rawContent);
            response.setContent(htmlContent);
            response.setSuccess(true);
        } else {
            try {
                // 调用LLM生成完整文章
                String llmResult = callLlmApi(request);
                // 解析LLM返回内容，格式：标题：xxx\n正文：yyy
                String title = topic;
                String content = llmResult;
                if (llmResult != null && llmResult.contains("标题：") && llmResult.contains("正文：")) {
                    String[] parts = llmResult.split("标题：", 2);
                    if (parts.length == 2) {
                        String[] subParts = parts[1].split("正文：", 2);
                        if (subParts.length == 2) {
                            title = subParts[0].replaceAll("[\n\r]", "").trim();
                            content = subParts[1].trim();
                        }
                    }
                }
                response.setTitle(title);
                response.setContent(content);
                response.setSuccess(true);
            } catch (Exception e) {
                log.error("LLM API调用失败，使用本地模板 fallback: {}", e.getMessage());
                response.setTitle(topic);
                String rawContent = generateContentFromTemplate(request);
                // 将Markdown转换为HTML
                String htmlContent = convertMarkdownToHtml(rawContent);
                response.setContent(htmlContent);
                response.setWarning("LLM API调用失败，已使用本地模板生成内容");
                response.setSuccess(false);
            }
        }
        return response;
    }

    @Override
    public ContentResponse convertContent(ContentRequest request, String platformType) {
        ContentResponse response = new ContentResponse();
        response.setTitle(request.getTitle());
        response.setGenerateTime(System.currentTimeMillis());

        String content = request.getContent();
        switch (platformType.toLowerCase()) {
            case "wechat":
                // 公众号格式：添加关注引导
                content += "\n\n点击关注获取更多干货～";
                break;
            case "douyin":
                // 抖音脚本格式：添加标记
                content = content
                        .replaceAll("\n\n", "\n[转场]\n")
                        .replaceAll("(\n|^)([^\n]+)", "\n[字幕]$2")
                        .replaceAll("\\*\\*(.*?)\\*\\*", "[重点]$1[/重点]");
                break;
            case "xiaohongshu":
                // 小红书格式：添加emoji和话题标签
                String[] keywords = request.getKeyword().split(" ");
                StringBuilder tags = new StringBuilder();
                for (int i = 0; i < Math.min(3, keywords.length); i++) {
                    tags.append(" #").append(keywords[i]);
                }
                content = "✨ " + content.replaceAll("\n\n", "\n\n✨ ") + tags;
                break;
            // 默认格式不做处理
        }

        response.setContent(content);
        return response;
    }

    @Override
    public String[] getTopicsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getRandomTopics(5);
        }

        keyword = keyword.toLowerCase();
        List<String> matchedTopics = new ArrayList<>();

        // 查找匹配的领域
        for (Map.Entry<String, List<String>> entry : topicMap.entrySet()) {
            if (entry.getKey().contains(keyword)) {
                matchedTopics.addAll(entry.getValue());
            }
        }

        // 如果没有找到匹配的，返回随机选题
        if (matchedTopics.isEmpty()) {
            return getRandomTopics(5);
        }

        // 随机选择5个选题
        Collections.shuffle(matchedTopics);
        return matchedTopics.stream()
                .limit(5)
                .toArray(String[]::new);
    }

    // 从JSON文件加载选题数据
    private Map<String, List<String>> loadTopicsFromJson() {
        try {
            ClassPathResource resource = new ClassPathResource("topics.json");
            InputStream inputStream = resource.getInputStream();
            String jsonContent = new String(FileCopyUtils.copyToByteArray(inputStream), StandardCharsets.UTF_8);
            return objectMapper.readValue(jsonContent, objectMapper.getTypeFactory().constructMapType(Map.class, String.class, List.class));
        } catch (IOException e) {
            log.error("加载选题数据失败", e);
            return createDefaultTopics();
        }
    }

    // 创建默认选题数据
    private Map<String, List<String>> createDefaultTopics() {
        Map<String, List<String>> defaultTopics = new HashMap<>();
        defaultTopics.put("food", Arrays.asList(
                "2025年低脂早餐食谱合集",
                "打工人带饭不翻车指南",
                "5分钟快手健康沙拉做法"
        ));
        defaultTopics.put("travel", Arrays.asList(
                "2025年小众旅行地推荐",
                "周末短途游：2小时高铁可达目的地"
        ));
        return defaultTopics;
    }

    // 获取随机选题
    private String[] getRandomTopics(int count) {
        List<String> allTopics = new ArrayList<>();
        for (List<String> topics : topicMap.values()) {
            allTopics.addAll(topics);
        }
        Collections.shuffle(allTopics);
        return allTopics.stream()
                .limit(count)
                .toArray(String[]::new);
    }

    // 生成标题
    private String generateTitle(String keyword) {
        String[] prefixes = {"2025年", "最新", "超实用", "干货分享："};
        String[] suffixes = {"完全指南", "技巧大全", "实战经验", "入门到精通"};

        String prefix = prefixes[new Random().nextInt(prefixes.length)];
        String suffix = suffixes[new Random().nextInt(suffixes.length)];

        return prefix + keyword + suffix;
    }

    // 调用LLM API生成完整文章，要求返回格式：标题：xxx\n正文：yyy
    private String callLlmApi(ContentRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + llmApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "doubao-pro-12b");
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", request.getWordCount());

        // 重点：prompt要求生成完整文章
        String prompt = String.format(
            "请以'%s'为题，写一篇%d字左右的完整中文文章，内容要有结构、有观点、有案例，适合自媒体发布。请返回格式：\\n标题：xxx\\n正文：yyy",
            request.getKeyword(), request.getWordCount());

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String response = restTemplate.postForObject(llmApiUrl, entity, String.class);

        // 解析API响应
        JsonNode rootNode = objectMapper.readTree(response);
        return rootNode.path("choices").get(0).path("message").path("content").asText();
    }

    // 使用本地模板生成内容
    private String generateContentFromTemplate(ContentRequest request) {
        try {
            // 加载模板文件
            ClassPathResource resource = new ClassPathResource("templates/content-template.md");
            InputStream inputStream = resource.getInputStream();
            String template = new String(FileCopyUtils.copyToByteArray(inputStream), StandardCharsets.UTF_8);

            // 替换模板变量
            Map<String, String> variables = createTemplateVariables(request);
            Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}||\\[\\[(.*?)\\]\\]");
            Matcher matcher = pattern.matcher(template);
            StringBuffer result = new StringBuffer();

            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    // 处理{{variable}}格式
                    String variable = matcher.group(1);
                    matcher.appendReplacement(result, variables.getOrDefault(variable, "{{" + variable + "}}"));
                } else if (matcher.group(2) != null) {
                    // 处理[[function]]格式
                    String function = matcher.group(2);
                    matcher.appendReplacement(result, executeTemplateFunction(function, request));
                }
            }
            matcher.appendTail(result);

            return result.toString();
        } catch (IOException e) {
            log.error("加载内容模板失败", e);
            return generateFallbackContent(request);
        }
    }

    // 创建模板变量
    private Map<String, String> createTemplateVariables(ContentRequest request) {
        Map<String, String> variables = new HashMap<>();
        variables.put("keyword", request.getKeyword());
        variables.put("title", generateTitle(request.getKeyword()));
        variables.put("currentYear", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        variables.put("author", "智能创作助手");
        variables.put("percentage", String.valueOf(new Random().nextInt(50) + 30) + "%");

        // 根据难度调整内容复杂度
        if ("easy".equals(request.getDifficulty())) {
            variables.put("complexity", "简单易懂的");
            variables.put("detailLevel", "基础");
        } else if ("hard".equals(request.getDifficulty())) {
            variables.put("complexity", "深入详细的");
            variables.put("detailLevel", "高级");
        } else {
            variables.put("complexity", "全面的");
            variables.put("detailLevel", "中级");
        }

        return variables;
    }

    // 执行模板函数
    private String executeTemplateFunction(String function, ContentRequest request) {
        switch (function) {
            case "randomScenario":
                String[] scenarios = {"日常生活中", "工作场合", "学习过程中", "社交聚会时"};
                return scenarios[new Random().nextInt(scenarios.length)];
            case "randomTip":
                String[] tips = {
                    "保持内容原创性是吸引读者的关键",
                    "定期更新内容可以提高粉丝活跃度",
                    "与读者互动能有效提升内容传播力",
                    "优质的封面图可以提高内容打开率"
                };
                return tips[new Random().nextInt(tips.length)];
            case "topicTags":
                String[] keywords = request.getKeyword().split(" ");
                StringBuilder tags = new StringBuilder();
                for (int i = 0; i < Math.min(3, keywords.length); i++) {
                    tags.append(" #").append(keywords[i]);
                }
                return tags.toString();
            default:
                return "[[" + function + "]]";
        }
    }

    // 生成备用内容
    private String generateFallbackContent(ContentRequest request) {
        return String.format(
            "# %s\n\n" +
            "## 什么是%s？\n\n" +
            "%s是当前非常热门的话题，受到了越来越多人的关注。据统计，相关内容的关注度同比增长了%d%%。\n\n" +
            "## %s的应用场景\n\n" +
            "1. 在日常生活中，%s可以帮助我们...\n\n" +
            "2. 在工作场合，%s能够提升我们的效率...\n\n" +
            "## 如何开始使用%s？\n\n" +
            "1. 了解基本概念\n" +
            "2. 掌握核心技巧\n" +
            "3. 实践并总结经验\n\n" +
            "希望本文对您有所帮助！#%s #内容创作",
            generateTitle(request.getKeyword()),
            request.getKeyword(),
            request.getKeyword(),
            new Random().nextInt(50) + 30,
            request.getKeyword(),
            request.getKeyword(),
            request.getKeyword(),
            request.getKeyword(),
            request.getKeyword()
        );
    }

    // 将Markdown转换为HTML
    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "<p>内容为空</p>";
        }
        
        // 简单的Markdown到HTML转换
        String html = markdown
            // 标题转换
            .replaceAll("^# (.+)$", "<h1>$1</h1>")
            .replaceAll("^## (.+)$", "<h2>$1</h2>")
            .replaceAll("^### (.+)$", "<h3>$1</h3>")
            // 粗体
            .replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>")
            // 斜体
            .replaceAll("\\*(.+?)\\*", "<em>$1</em>")
            // 列表
            .replaceAll("^\\d+\\. (.+)$", "<li>$1</li>")
            .replaceAll("^- (.+)$", "<li>$1</li>")
            // 段落
            .replaceAll("\n\n", "</p><p>")
            .replaceAll("^(.+)$", "<p>$1</p>");
        
        // 清理多余的标签
        html = html.replaceAll("<p></p>", "");
        html = html.replaceAll("<p><p>", "<p>");
        html = html.replaceAll("</p></p>", "</p>");
        
        return html;
    }

    @Override
    public ContentResponse saveContent(ContentRequest request) {
        ContentResponse response = new ContentResponse();
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                response.setSuccess(false);
                response.setWarning("用户未登录，无法保存内容");
                return response;
            }

            // 从JWT token获取用户信息
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElse(null);
            
            if (user == null) {
                response.setSuccess(false);
                response.setWarning("用户信息不存在，无法保存内容");
                return response;
            }

            Content content = new Content();
            content.setTitle(request.getTitle());
            content.setContent(request.getContent());
            
            // 设置创建者ID
            content.setCreatedBy(user.getId());
            
            // 设置topicId，如果keyword是数字则使用，否则设置为默认值1
            if (request.getKeyword() != null) {
                try {
                    Long topicId = Long.parseLong(request.getKeyword());
                    content.setTopicId(topicId);
                } catch (NumberFormatException e) {
                    // 如果keyword不是数字，设置为默认topicId
                    content.setTopicId(1L);
                }
            } else {
                content.setTopicId(1L);
            }
            
            content.setIsPublished(true);
            Content saved = contentRepository.save(content);
            response.setId(saved.getId());
            response.setTitle(saved.getTitle());
            response.setContent(saved.getContent());
            response.setSuccess(true);
        } catch (Exception e) {
            log.error("保存内容失败", e);
            response.setSuccess(false);
            response.setWarning("内容保存失败: " + e.getMessage());
        }
        return response;
    }
}