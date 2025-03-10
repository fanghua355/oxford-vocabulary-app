package com.oxford.vocabulary;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.oxford.vocabulary.mapper")
@EnableCaching
public class OxfordVocabularyApplication extends SpringBootServletInitializer {

    private static final Logger log = LoggerFactory.getLogger(OxfordVocabularyApplication.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OxfordVocabularyApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(OxfordVocabularyApplication.class, args);
    }
} 