package com.oxford.vocabulary.mapper;

import com.oxford.vocabulary.entity.Vocabulary;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VocabularyMapper {
    
    @Insert("INSERT INTO vocabulary (word, phonetic, translation, level, example, part_of_speech, definition, synonyms, antonyms) " +
            "VALUES (#{word}, #{phonetic}, #{translation}, #{level}, #{example}, #{partOfSpeech}, #{definition}, #{synonyms}, #{antonyms})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Vocabulary vocabulary);

    @Insert("<script>" +
            "INSERT INTO vocabulary (word, phonetic, translation, level, example, part_of_speech, definition, synonyms, antonyms) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.word}, #{item.phonetic}, #{item.translation}, #{item.level}, #{item.example}, " +
            "#{item.partOfSpeech}, #{item.definition}, #{item.synonyms}, #{item.antonyms})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<Vocabulary> vocabularies);

    @Select("SELECT * FROM vocabulary WHERE id = #{id}")
    Vocabulary findById(@Param("id") Long id);

    @Select("SELECT * FROM vocabulary WHERE word = #{word}")
    Vocabulary findByWord(@Param("word") String word);

    @Select("SELECT * FROM vocabulary")
    List<Vocabulary> findAll();

    @Select("SELECT * FROM vocabulary WHERE level = #{level}")
    List<Vocabulary> findByLevel(@Param("level") String level);
    
    @Select("SELECT COUNT(*) FROM vocabulary")
    int count();
    
    @Update("TRUNCATE TABLE vocabulary")
    void truncateTable();
    
    @Select("SELECT * FROM vocabulary WHERE word LIKE CONCAT('%', #{keyword}, '%') " +
            "OR translation LIKE CONCAT('%', #{keyword}, '%') " +
            "OR definition LIKE CONCAT('%', #{keyword}, '%')")
    List<Vocabulary> searchByKeyword(@Param("keyword") String keyword);
} 