package com.oxford.vocabulary.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "study_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "word_id"})
})
public class StudyProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyStatus status;
    
    @Column(nullable = false)
    private LocalDate lastUpdated;
    
    public enum StudyStatus {
        NOT_STARTED,
        LEARNING,
        LEARNED
    }

    public StudyProgress() {
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

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
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