@Select("SELECT * FROM vocabulary WHERE word LIKE CONCAT('%', #{keyword}, '%') " +
        "OR translation LIKE CONCAT('%', #{keyword}, '%') " +
        "OR definition LIKE CONCAT('%', #{keyword}, '%') " +
        "LIMIT #{offset}, #{pageSize}")
List<Vocabulary> searchByKeywordPaged(@Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);

@Select("SELECT COUNT(*) FROM vocabulary WHERE word LIKE CONCAT('%', #{keyword}, '%') " +
        "OR translation LIKE CONCAT('%', #{keyword}, '%') " +
        "OR definition LIKE CONCAT('%', #{keyword}, '%')")
int countByKeyword(@Param("keyword") String keyword); 