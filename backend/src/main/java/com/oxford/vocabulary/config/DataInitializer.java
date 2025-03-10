package com.oxford.vocabulary.config;

import com.oxford.vocabulary.model.Word;
import com.oxford.vocabulary.repository.WordRepository;
import com.oxford.vocabulary.util.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final String INIT_MARKER_FILE = ".data_initialized";
    private final WordRepository wordRepository;

    @Autowired
    public DataInitializer(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Data initialization is disabled for WAR deployment");
        // 禁用数据初始化，因为数据库中已经有数据
        return;
    }
} 