package com.oxford.vocabulary.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vocabulary")
public class VocabularyController {

    @GetMapping("/search")
    public ResponseEntity<List<Vocabulary>> searchVocabularies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Vocabulary> results = vocabularyMapper.searchByKeywordPaged(keyword, page * size, size);
        int total = vocabularyMapper.countByKeyword(keyword);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(total))
                .body(results);
    }
} 