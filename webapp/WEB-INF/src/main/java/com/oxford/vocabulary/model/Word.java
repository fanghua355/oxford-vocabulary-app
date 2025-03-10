package com.oxford.vocabulary.model;

import javax.persistence.*;
import org.hibernate.annotations.BatchSize;
import java.util.*;
import java.util.Objects;

@Entity
@Table(name = "words", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"word", "part_of_speech"})
})
public class Word {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false)
    private String word;
    
    private String phonetic;
    
    private String translation;
    
    private String level;
    
    @Column(length = 1000)
    private String example;
    
    @Column(name = "part_of_speech")
    private String partOfSpeech;
    
    @Column(length = 1000)
    private String definition;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "word_synonyms", joinColumns = @JoinColumn(name = "word_id"))
    @Column(name = "synonym")
    @BatchSize(size = 50)
    private Set<String> synonyms = new HashSet<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "word_antonyms", joinColumns = @JoinColumn(name = "word_id"))
    @Column(name = "antonym")
    @BatchSize(size = 50)
    private Set<String> antonyms = new HashSet<>();

    public Word() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Set<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<String> synonyms) {
        this.synonyms = synonyms;
    }

    public Set<String> getAntonyms() {
        return antonyms;
    }

    public void setAntonyms(Set<String> antonyms) {
        this.antonyms = antonyms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(this.word, word.word) &&
               Objects.equals(this.partOfSpeech, word.partOfSpeech);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, partOfSpeech);
    }
} 