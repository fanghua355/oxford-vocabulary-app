package com.oxford.vocabulary;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.oxford.vocabulary.mapper")
public class OxfordVocabularyApplication {

    private static final Logger log = LoggerFactory.getLogger(OxfordVocabularyApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OxfordVocabularyApplication.class, args);
    }
} 