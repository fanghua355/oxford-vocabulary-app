package com.oxford.vocabulary.repository;

import com.oxford.vocabulary.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByLevel(String level);
    List<Word> findByWordContainingIgnoreCaseOrTranslationContainingIgnoreCase(String wordKeyword, String translationKeyword);
} 