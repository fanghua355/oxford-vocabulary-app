package com.oxford.vocabulary.service;

import com.oxford.vocabulary.model.StudyProgress.StudyStatus;
import com.oxford.vocabulary.model.dto.StudyProgressDTO;

import java.util.List;
import java.util.Map;

public interface StudyProgressService {
    List<StudyProgressDTO> getUserProgress(Long userId);
    StudyProgressDTO getProgressByUserAndWord(Long userId, Long wordId);
    StudyProgressDTO updateProgress(Long userId, Long wordId, StudyStatus status);
    Map<String, Object> getUserStatistics(Long userId);
} 