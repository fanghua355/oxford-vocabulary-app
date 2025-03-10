package com.oxford.vocabulary.controller;

import com.oxford.vocabulary.model.StudyProgress.StudyStatus;
import com.oxford.vocabulary.model.dto.StudyProgressDTO;
import com.oxford.vocabulary.service.StudyProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/progress")
public class StudyProgressController {

    private final StudyProgressService studyProgressService;

    @Autowired
    public StudyProgressController(StudyProgressService studyProgressService) {
        this.studyProgressService = studyProgressService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StudyProgressDTO>> getUserProgress(@PathVariable Long userId) {
        return ResponseEntity.ok(studyProgressService.getUserProgress(userId));
    }

    @GetMapping("/user/{userId}/word/{wordId}")
    public ResponseEntity<StudyProgressDTO> getProgressByUserAndWord(
            @PathVariable Long userId,
            @PathVariable Long wordId) {
        StudyProgressDTO progress = studyProgressService.getProgressByUserAndWord(userId, wordId);
        if (progress == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/user/{userId}/word/{wordId}")
    public ResponseEntity<StudyProgressDTO> updateProgress(
            @PathVariable Long userId,
            @PathVariable Long wordId,
            @RequestParam StudyStatus status) {
        return ResponseEntity.ok(studyProgressService.updateProgress(userId, wordId, status));
    }

    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable Long userId) {
        return ResponseEntity.ok(studyProgressService.getUserStatistics(userId));
    }
} 