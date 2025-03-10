package com.oxford.vocabulary.util;

import com.oxford.vocabulary.service.VocabularyImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;

@Configuration
public class DataImportTool {

    private static final Logger log = LoggerFactory.getLogger(DataImportTool.class);

    @Bean
    @Profile("import")
    public CommandLineRunner importData(VocabularyImportService importService) {
        return args -> {
            log.info("Starting data import...");
            importService.importFromCsv("oxford3000.csv");
            log.info("Data import completed.");
            System.exit(0);
        };
    }
} 