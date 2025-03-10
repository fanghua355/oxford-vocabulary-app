-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空表
TRUNCATE TABLE vocabulary;

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入数据示例
INSERT INTO vocabulary (word, phonetic, translation, level, example, part_of_speech, definition, synonyms, antonyms)
VALUES 
('birth', '/bɜːθ/', '出生', 'B1', 'It was a difficult birth.', 'noun', 'the time when a baby or young animal comes out of its mother''s body:', 'give birth', NULL),
('apartment', '/əˈpɑːt.mənt/', '公寓', 'B1', 'I''ll give you the keys to my apartment.', 'noun', 'a set of rooms for living in, especially on one floor of a building:', NULL, NULL);

-- 注意：完整的数据将通过Java代码从CSV文件导入 