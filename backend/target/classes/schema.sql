CREATE TABLE IF NOT EXISTS vocabulary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    word VARCHAR(255) NOT NULL,
    phonetic VARCHAR(255),
    translation TEXT,
    level VARCHAR(50),
    example TEXT,
    part_of_speech VARCHAR(50),
    definition TEXT,
    synonyms TEXT,
    antonyms TEXT,
    INDEX idx_word (word),
    INDEX idx_level (level),
    FULLTEXT INDEX idx_search (word, translation, definition)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 