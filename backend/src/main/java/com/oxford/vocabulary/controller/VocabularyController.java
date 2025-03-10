package com.oxford.vocabulary.controller;

import com.oxford.vocabulary.entity.Vocabulary;
import com.oxford.vocabulary.mapper.VocabularyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/words")
public class VocabularyController {

    private final VocabularyMapper vocabularyMapper;

    @Autowired
    public VocabularyController(VocabularyMapper vocabularyMapper) {
        this.vocabularyMapper = vocabularyMapper;
    }

    @GetMapping
    public ResponseEntity<List<Vocabulary>> getAllVocabularies() {
        return ResponseEntity.ok(vocabularyMapper.findAll());
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
    public ResponseEntity<List<Vocabulary>> getVocabulariesByLevel(@PathVariable String level) {
        return ResponseEntity.ok(vocabularyMapper.findByLevel(level));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Vocabulary>> searchVocabularies(@RequestParam String keyword) {
        return ResponseEntity.ok(vocabularyMapper.searchByKeyword(keyword));
    }
} 