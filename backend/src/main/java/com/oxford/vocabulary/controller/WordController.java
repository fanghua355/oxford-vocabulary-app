package com.oxford.vocabulary.controller;

import com.oxford.vocabulary.model.dto.WordDTO;
import com.oxford.vocabulary.service.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/words")
public class WordController {

    private static final Logger logger = LoggerFactory.getLogger(WordController.class);
    private final WordService wordService;

    @Autowired
    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping
    public ResponseEntity<List<WordDTO>> getAllWords() {
        List<WordDTO> words = wordService.getAllWords();
        logger.info("Returning {} words from getAllWords endpoint", words.size());
        return ResponseEntity.ok(words);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<WordDTO>> getPagedWords(Pageable pageable) {
        return ResponseEntity.ok(wordService.getPagedWords(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordDTO> getWordById(@PathVariable Long id) {
        return ResponseEntity.ok(wordService.getWordById(id));
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<WordDTO>> getWordsByLevel(@PathVariable String level) {
        return ResponseEntity.ok(wordService.getWordsByLevel(level));
    }

    @GetMapping("/search")
    public ResponseEntity<List<WordDTO>> searchWords(@RequestParam String keyword) {
        return ResponseEntity.ok(wordService.searchWords(keyword));
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Long>> getTotalWords() {
        long total = wordService.getTotalWords();
        return ResponseEntity.ok(Map.of("total", total));
    }

    @PostMapping
    public ResponseEntity<WordDTO> createWord(@Valid @RequestBody WordDTO wordDTO) {
        return new ResponseEntity<>(wordService.createWord(wordDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WordDTO> updateWord(@PathVariable Long id, @Valid @RequestBody WordDTO wordDTO) {
        return ResponseEntity.ok(wordService.updateWord(id, wordDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
} 