package com.media.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // 设置连接超时时间为5秒
        requestFactory.setConnectTimeout(5000);
        // 设置读取超时时间为30秒
        requestFactory.setReadTimeout(30000);
        return new RestTemplate(requestFactory);
    }
}