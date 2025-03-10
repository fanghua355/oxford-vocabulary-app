package com.oxford.vocabulary.repository;

import com.oxford.vocabulary.model.StudyProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyProgressRepository extends JpaRepository<StudyProgress, Long> {
    List<StudyProgress> findByUserId(Long userId);
    Optional<StudyProgress> findByUserIdAndWordId(Long userId, Long wordId);
} 