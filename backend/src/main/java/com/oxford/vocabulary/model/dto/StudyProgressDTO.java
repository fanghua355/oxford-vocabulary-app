package com.oxford.vocabulary.model.dto;

import com.oxford.vocabulary.model.StudyProgress.StudyStatus;
import java.time.LocalDate;

public class StudyProgressDTO {
    private Long id;
    private Long userId;
    private Long wordId;
    private StudyStatus status;
    private LocalDate lastUpdated;

    public StudyProgressDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public StudyStatus getStatus() {
        return status;
    }

    public void setStatus(StudyStatus status) {
        this.status = status;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
} 