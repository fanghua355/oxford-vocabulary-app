package com.oxford.vocabulary.service.impl;

import com.oxford.vocabulary.model.Word;
import com.oxford.vocabulary.model.dto.WordDTO;
import com.oxford.vocabulary.repository.WordRepository;
import com.oxford.vocabulary.service.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WordServiceImpl implements WordService {

    private static final Logger logger = LoggerFactory.getLogger(WordServiceImpl.class);
    private final WordRepository wordRepository;

    @Autowired
    public WordServiceImpl(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allWords")
    public List<WordDTO> getAllWords() {
        logger.info("Getting all words from database");
        List<Word> words = wordRepository.findAll();
        logger.info("Found {} words in database", words.size());
        List<WordDTO> dtos = words.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Converted {} words to DTOs", dtos.size());
        return dtos;
    }

    @Override
    @Cacheable(value = "pagedWords", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<WordDTO> getPagedWords(Pageable pageable) {
        return wordRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    @Cacheable(value = "words", key = "#id")
    public WordDTO getWordById(Long id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Word not found with id: " + id));
        return convertToDTO(word);
    }

    @Override
    @Cacheable(value = "wordsByLevel", key = "#level")
    public List<WordDTO> getWordsByLevel(String level) {
        return wordRepository.findByLevel(level).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "searchWords", key = "#keyword")
    public List<WordDTO> searchWords(String keyword) {
        return wordRepository.findByWordContainingIgnoreCaseOrTranslationContainingIgnoreCase(keyword, keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allWords", "pagedWords", "words", "wordsByLevel", "searchWords"}, allEntries = true)
    public WordDTO createWord(WordDTO wordDTO) {
        Word word = convertToEntity(wordDTO);
        Word savedWord = wordRepository.save(word);
        return convertToDTO(savedWord);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allWords", "pagedWords", "words", "wordsByLevel", "searchWords"}, allEntries = true)
    public WordDTO updateWord(Long id, WordDTO wordDTO) {
        Word existingWord = wordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Word not found with id: " + id));
        
        // 更新基本字段
        existingWord.setWord(wordDTO.getWord());
        existingWord.setPhonetic(wordDTO.getPhonetic());
        existingWord.setTranslation(wordDTO.getTranslation());
        existingWord.setLevel(wordDTO.getLevel());
        existingWord.setExample(wordDTO.getExample());
        existingWord.setPartOfSpeech(wordDTO.getPartOfSpeech());
        existingWord.setDefinition(wordDTO.getDefinition());
        
        // 更新集合
        existingWord.getSynonyms().clear();
        if (wordDTO.getSynonyms() != null) {
            existingWord.getSynonyms().addAll(wordDTO.getSynonyms());
        }
        
        existingWord.getAntonyms().clear();
        if (wordDTO.getAntonyms() != null) {
            existingWord.getAntonyms().addAll(wordDTO.getAntonyms());
        }
        
        Word updatedWord = wordRepository.save(existingWord);
        return convertToDTO(updatedWord);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allWords", "pagedWords", "words", "wordsByLevel", "searchWords"}, allEntries = true)
    public void deleteWord(Long id) {
        if (!wordRepository.existsById(id)) {
            throw new EntityNotFoundException("Word not found with id: " + id);
        }
        wordRepository.deleteById(id);
    }

    @Override
    public long getTotalWords() {
        return wordRepository.count();
    }

    private WordDTO convertToDTO(Word word) {
        WordDTO dto = new WordDTO();
        BeanUtils.copyProperties(word, dto);
        return dto;
    }

    private Word convertToEntity(WordDTO dto) {
        Word entity = new Word();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
} 