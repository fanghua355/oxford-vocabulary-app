package com.oxford.vocabulary.controller;

import com.oxford.vocabulary.dto.PageResponse;
import com.oxford.vocabulary.entity.Vocabulary;
import com.oxford.vocabulary.mapper.VocabularyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
public class VocabularyController {

    private final VocabularyMapper vocabularyMapper;
    private static final int DEFAULT_PAGE_SIZE = 50;

    @Autowired
    public VocabularyController(VocabularyMapper vocabularyMapper) {
        this.vocabularyMapper = vocabularyMapper;
    }

    @GetMapping
    @Cacheable(value = "vocabularies", key = "'all_' + #page + '_' + #size", sync = true)
    public ResponseEntity<PageResponse<Vocabulary>> getAllVocabularies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        // 限制页面大小，防止请求过大数据量
        if (size > 100) {
            size = 100;
        }
        
        int offset = page * size;
        List<Vocabulary> vocabularies = vocabularyMapper.findAllPaginated(offset, size);
        int totalCount = vocabularyMapper.count();
        
        if (vocabularies.isEmpty() && page > 0) {
            return ResponseEntity.noContent().build();
        }
        
        PageResponse<Vocabulary> response = new PageResponse<>(vocabularies, page, size, totalCount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{wordOrId}")
    @Cacheable(value = "vocabulary", key = "#wordOrId", sync = true)
    public ResponseEntity<Vocabulary> getVocabularyByWordOrId(@PathVariable String wordOrId) {
        Vocabulary vocabulary = null;
        
        // 尝试将参数解析为ID
        try {
            Long id = Long.parseLong(wordOrId);
            vocabulary = vocabularyMapper.findById(id);
        } catch (NumberFormatException e) {
            // 如果不是ID，则按单词查询
            vocabulary = vocabularyMapper.findByWord(wordOrId);
        }
        
        if (vocabulary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vocabulary);
    }

    @GetMapping("/level/{level}")
    @Cacheable(value = "vocabulariesByLevel", key = "#level + '_' + #page + '_' + #size", sync = true)
    public ResponseEntity<PageResponse<Vocabulary>> getVocabulariesByLevel(
            @PathVariable String level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        // 限制页面大小，防止请求过大数据量
        if (size > 100) {
            size = 100;
        }
        
        int offset = page * size;
        List<Vocabulary> vocabularies = vocabularyMapper.findByLevelPaginated(level, offset, size);
        int totalCount = vocabularyMapper.countByLevel(level);
        
        if (vocabularies.isEmpty() && page > 0) {
            return ResponseEntity.noContent().build();
        }
        
        PageResponse<Vocabulary> response = new PageResponse<>(vocabularies, page, size, totalCount);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @Cacheable(value = "vocabulariesByKeyword", key = "#keyword + '_' + #page + '_' + #size", sync = true)
    public ResponseEntity<PageResponse<Vocabulary>> searchVocabularies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        // 限制页面大小，防止请求过大数据量
        if (size > 100) {
            size = 100;
        }
        
        int offset = page * size;
        List<Vocabulary> vocabularies = vocabularyMapper.searchByKeywordPaginated(keyword, offset, size);
        int totalCount = vocabularyMapper.countByKeyword(keyword);
        
        if (vocabularies.isEmpty() && page > 0) {
            return ResponseEntity.noContent().build();
        }
        
        PageResponse<Vocabulary> response = new PageResponse<>(vocabularies, page, size, totalCount);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cache/clear")
    @Caching(evict = {
        @CacheEvict(value = "vocabularies", allEntries = true),
        @CacheEvict(value = "vocabulary", allEntries = true),
        @CacheEvict(value = "vocabulariesByLevel", allEntries = true),
        @CacheEvict(value = "vocabulariesByKeyword", allEntries = true)
    })
    public ResponseEntity<Void> clearCache() {
        return ResponseEntity.ok().build();
    }
} 