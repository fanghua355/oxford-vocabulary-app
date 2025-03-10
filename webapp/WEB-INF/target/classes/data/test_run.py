from PyPDF2 import process_pdf

# 处理前100个单词作为测试
process_pdf('translations_20250302.pdf', 'oxford3000_test.csv', 'oxford3000_test_cache.pkl', max_words=100) 