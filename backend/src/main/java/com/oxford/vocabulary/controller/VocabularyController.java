package com.oxford.vocabulary.controller;

import com.oxford.vocabulary.entity.Vocabulary;
import com.oxford.vocabulary.mapper.VocabularyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
public class VocabularyController {

    private final VocabularyMapper vocabularyMapper;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    public VocabularyController(VocabularyMapper vocabularyMapper) {
        this.vocabularyMapper = vocabularyMapper;
    }

    @GetMapping
    public ResponseEntity<List<Vocabulary>> getAllVocabularies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "asc") String sort) {
        String sortDirection = sort.equalsIgnoreCase("desc") ? "DESC" : "ASC";
        List<Vocabulary> results = vocabularyMapper.findAllPaged(page * size, size, sortDirection);
        int total = vocabularyMapper.count();
        return ResponseEntity.ok()
                .header("x-total-count", String.valueOf(total))
                .body(results);
    }

    @GetMapping("/{wordOrId}")
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
    public ResponseEntity<List<Vocabulary>> getVocabulariesByLevel(
            @PathVariable String level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "asc") String sort) {
        String sortDirection = sort.equalsIgnoreCase("desc") ? "DESC" : "ASC";
        List<Vocabulary> results = vocabularyMapper.findByLevelPaged(level, page * size, size, sortDirection);
        int total = vocabularyMapper.countByLevel(level);
        return ResponseEntity.ok()
                .header("x-total-count", String.valueOf(total))
                .body(results);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Vocabulary>> searchVocabularies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "asc") String sort) {
        String sortDirection = sort.equalsIgnoreCase("desc") ? "DESC" : "ASC";
        List<Vocabulary> results = vocabularyMapper.searchByKeywordPaged(keyword, page * size, size, sortDirection);
        int total = vocabularyMapper.countByKeyword(keyword);
        return ResponseEntity.ok()
                .header("x-total-count", String.valueOf(total))
                .body(results);
    }
} 