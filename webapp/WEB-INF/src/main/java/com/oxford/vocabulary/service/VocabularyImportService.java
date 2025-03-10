package com.oxford.vocabulary.service;

import com.oxford.vocabulary.entity.Vocabulary;
import com.oxford.vocabulary.mapper.VocabularyMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VocabularyImportService {

    private static final Logger log = LoggerFactory.getLogger(VocabularyImportService.class);
    private final VocabularyMapper vocabularyMapper;
    private static final int BATCH_SIZE = 500;

    @Autowired
    public VocabularyImportService(VocabularyMapper vocabularyMapper) {
        this.vocabularyMapper = vocabularyMapper;
    }

    @Transactional
    public void importFromCsv(String csvFileName) {
        try {
            // 首先检查数据库中是否已有数据
            int existingCount = vocabularyMapper.count();
            if (existingCount > 0) {
                log.info("数据库中已存在 {} 条记录，将执行清空操作", existingCount);
                vocabularyMapper.truncateTable();
                log.info("数据库表已清空");
            }

            ClassPathResource resource = new ClassPathResource("data/" + csvFileName);
            try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
                // Skip header
                reader.readNext();

                List<Vocabulary> batch = new ArrayList<>();
                String[] line;
                int count = 0;
                Set<String> processedWords = new HashSet<>(); // 用于检测CSV文件中的重复单词

                while ((line = reader.readNext()) != null) {
                    String word = line[0].trim();
                    
                    // 跳过CSV文件中的重复单词
                    if (processedWords.contains(word)) {
                        log.warn("跳过重复单词: {}", word);
                        continue;
                    }
                    
                    processedWords.add(word);
                    
                    Vocabulary vocabulary = new Vocabulary();
                    vocabulary.setWord(word);
                    vocabulary.setPhonetic(line[1]);
                    vocabulary.setTranslation(line[2]);
                    vocabulary.setLevel(line[3]);
                    vocabulary.setExample(line[4]);
                    vocabulary.setPartOfSpeech(line[5]);
                    vocabulary.setDefinition(line[6]);
                    vocabulary.setSynonyms(line.length > 7 ? line[7] : null);
                    vocabulary.setAntonyms(line.length > 8 ? line[8] : null);

                    batch.add(vocabulary);
                    count++;

                    if (batch.size() >= BATCH_SIZE) {
                        try {
                            vocabularyMapper.batchInsert(batch);
                        } catch (DuplicateKeyException e) {
                            log.warn("批量插入时发生重复键异常，将改为逐条插入");
                            insertOneByOne(batch);
                        }
                        batch.clear();
                        log.info("已导入 {} 条记录", count);
                    }
                }

                if (!batch.isEmpty()) {
                    try {
                        vocabularyMapper.batchInsert(batch);
                    } catch (DuplicateKeyException e) {
                        log.warn("批量插入时发生重复键异常，将改为逐条插入");
                        insertOneByOne(batch);
                    }
                    log.info("已导入最后 {} 条记录", batch.size());
                }

                log.info("成功导入所有 {} 条记录", count);
            }
        } catch (IOException | CsvValidationException e) {
            log.error("导入CSV文件时出错: {}", e.getMessage(), e);
            throw new RuntimeException("导入CSV文件失败", e);
        }
    }
    
    private void insertOneByOne(List<Vocabulary> vocabularies) {
        int successCount = 0;
        for (Vocabulary vocabulary : vocabularies) {
            try {
                vocabularyMapper.insert(vocabulary);
                successCount++;
            } catch (DuplicateKeyException e) {
                log.warn("跳过重复单词: {}", vocabulary.getWord());
            }
        }
        log.info("逐条插入完成，成功: {}, 失败: {}", successCount, vocabularies.size() - successCount);
    }
} 