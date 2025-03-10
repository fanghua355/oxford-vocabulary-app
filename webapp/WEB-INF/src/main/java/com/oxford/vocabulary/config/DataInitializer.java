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
        Path markerFile = Paths.get(INIT_MARKER_FILE);
        long count = wordRepository.count();
        logger.info("Current database word count: {}", count);
        
        // 只有当标记文件存在且数据库中有数据时，才跳过初始化
        if (Files.exists(markerFile) && count > 0) {
            logger.info("Database already initialized (marker file exists and database has data)");
            return;
        }

        // 如果标记文件存在但数据库为空，删除标记文件
        if (Files.exists(markerFile) && count == 0) {
            try {
                Files.delete(markerFile);
                logger.info("Deleted stale marker file because database is empty");
            } catch (IOException e) {
                logger.warn("Failed to delete stale marker file: {}", e.getMessage());
            }
        }

        logger.info("Starting data initialization...");
        try {
            List<Word> words = CsvReader.readOxford3000Words();
            logger.info("Read {} words from CSV file", words.size());
            if (!words.isEmpty()) {
                // 使用 Map 来检查重复项
                Map<String, Word> uniqueWords = new HashMap<>();
                int duplicates = 0;
                for (Word word : words) {
                    String key = word.getWord() + "|" + word.getPartOfSpeech();
                    if (uniqueWords.containsKey(key)) {
                        duplicates++;
                        logger.warn("Duplicate word found: {} ({})", word.getWord(), word.getPartOfSpeech());
                    } else {
                        uniqueWords.put(key, word);
                    }
                }
                logger.info("Found {} duplicate words", duplicates);
                
                List<Word> uniqueWordList = new ArrayList<>(uniqueWords.values());
                List<Word> savedWords = wordRepository.saveAll(uniqueWordList);
                logger.info("Successfully imported {} words into database", savedWords.size());
                
                // 验证导入后的数据数量
                long newCount = wordRepository.count();
                logger.info("Verified database word count after import: {}", newCount);
                
                if (newCount != uniqueWordList.size()) {
                    logger.warn("Warning: Number of saved words ({}) differs from number of unique words ({})", 
                        newCount, uniqueWordList.size());
                }
                
                // 只有在成功导入数据后才创建标记文件
                if (newCount > 0) {
                    try {
                        Files.createFile(markerFile);
                        logger.info("Created initialization marker file");
                    } catch (IOException e) {
                        logger.warn("Failed to create initialization marker file: {}", e.getMessage());
                    }
                }
            } else {
                logger.warn("No words were read from the CSV file");
            }
        } catch (Exception e) {
            logger.error("Failed to initialize database: {}", e.getMessage(), e);
        }
    }
} 