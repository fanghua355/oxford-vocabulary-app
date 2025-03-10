package com.oxford.vocabulary.service.impl;

import com.oxford.vocabulary.model.StudyProgress;
import com.oxford.vocabulary.model.StudyProgress.StudyStatus;
import com.oxford.vocabulary.model.Word;
import com.oxford.vocabulary.model.dto.StudyProgressDTO;
import com.oxford.vocabulary.repository.StudyProgressRepository;
import com.oxford.vocabulary.repository.WordRepository;
import com.oxford.vocabulary.service.StudyProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudyProgressServiceImpl implements StudyProgressService {

    private final StudyProgressRepository studyProgressRepository;
    private final WordRepository wordRepository;

    @Autowired
    public StudyProgressServiceImpl(StudyProgressRepository studyProgressRepository, WordRepository wordRepository) {
        this.studyProgressRepository = studyProgressRepository;
        this.wordRepository = wordRepository;
    }

    @Override
    public List<StudyProgressDTO> getUserProgress(Long userId) {
        return studyProgressRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudyProgressDTO getProgressByUserAndWord(Long userId, Long wordId) {
        StudyProgress progress = studyProgressRepository.findByUserIdAndWordId(userId, wordId)
                .orElse(null);
        
        if (progress == null) {
            return null;
        }
        
        return convertToDTO(progress);
    }

    @Override
    @Transactional
    public StudyProgressDTO updateProgress(Long userId, Long wordId, StudyStatus status) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new EntityNotFoundException("Word not found with id: " + wordId));
        
        StudyProgress progress = studyProgressRepository.findByUserIdAndWordId(userId, wordId)
                .orElse(new StudyProgress());
        
        progress.setUserId(userId);
        progress.setWord(word);
        progress.setStatus(status);
        progress.setLastUpdated(LocalDate.now());
        
        StudyProgress savedProgress = studyProgressRepository.save(progress);
        return convertToDTO(savedProgress);
    }

    @Override
    public Map<String, Object> getUserStatistics(Long userId) {
        List<StudyProgress> userProgress = studyProgressRepository.findByUserId(userId);
        
        long totalWords = wordRepository.count();
        long learnedWords = userProgress.stream()
                .filter(p -> p.getStatus() == StudyStatus.LEARNED)
                .count();
        long learningWords = userProgress.stream()
                .filter(p -> p.getStatus() == StudyStatus.LEARNING)
                .count();
        
        Map<String, Long> levelDistribution = userProgress.stream()
                .filter(p -> p.getStatus() == StudyStatus.LEARNED)
                .collect(Collectors.groupingBy(
                        p -> p.getWord().getLevel(),
                        Collectors.counting()
                ));
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalWords", totalWords);
        statistics.put("learnedWords", learnedWords);
        statistics.put("learningWords", learningWords);
        statistics.put("progressPercentage", totalWords > 0 ? (learnedWords * 100.0 / totalWords) : 0);
        statistics.put("levelDistribution", levelDistribution);
        
        return statistics;
    }

    private StudyProgressDTO convertToDTO(StudyProgress progress) {
        StudyProgressDTO dto = new StudyProgressDTO();
        dto.setId(progress.getId());
        dto.setUserId(progress.getUserId());
        dto.setWordId(progress.getWord().getId());
        dto.setStatus(progress.getStatus());
        dto.setLastUpdated(progress.getLastUpdated());
        return dto;
    }
} 