package com.oxford.vocabulary.service;

import com.oxford.vocabulary.model.dto.WordDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WordService {
    List<WordDTO> getAllWords();
    Page<WordDTO> getPagedWords(Pageable pageable);
    WordDTO getWordById(Long id);
    List<WordDTO> getWordsByLevel(String level);
    List<WordDTO> searchWords(String keyword);
    WordDTO createWord(WordDTO wordDTO);
    WordDTO updateWord(Long id, WordDTO wordDTO);
    void deleteWord(Long id);
    long getTotalWords();
} 