package com.oxford.vocabulary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 移除 CORS 配置，使用 CorsConfig 类的配置
} 