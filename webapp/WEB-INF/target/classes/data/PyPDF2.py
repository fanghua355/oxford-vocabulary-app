import pdfplumber
import time
import os
import concurrent.futures
import threading
import re
import random
import json
import pickle
import csv
import requests
import ssl
from bs4 import BeautifulSoup
import nltk
import urllib.request
from nltk.corpus import wordnet

# 创建不验证 SSL 的上下文
ssl._create_default_https_context = ssl._create_unverified_context

# 下载必要的 NLTK 数据
try:
    nltk.data.find('corpora/wordnet')
except LookupError:
    try:
        print("正在下载 WordNet 数据...")
        nltk.download('wordnet', quiet=True)
        print("WordNet 数据下载完成")
    except Exception as e:
        print(f"WordNet 数据下载失败: {str(e)}")

def get_cambridge_data(word):
    """从 Cambridge Dictionary 获取单词信息"""
    try:
        url = f"https://dictionary.cambridge.org/dictionary/english/{word}"
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        response = requests.get(url, headers=headers, verify=False)  # 禁用 SSL 验证
        soup = BeautifulSoup(response.text, 'html.parser')
        return soup
    except Exception as e:
        print(f"访问 Cambridge Dictionary 失败 ({word}): {str(e)}")
        return None

def translate_word(word):
    """获取单词的中文翻译"""
    try:
        soup = get_cambridge_data(word)
        if soup:
            trans = soup.find('span', class_='trans')
            if trans:
                return trans.text.strip()
    except Exception as e:
        print(f"获取翻译失败 ({word}): {str(e)}")
    return ""

def get_phonetic(word):
    """获取单词的音标"""
    try:
        soup = get_cambridge_data(word)
        if soup:
            ipa = soup.find('span', class_='ipa')
            if ipa:
                return f"/{ipa.text}/"
    except Exception as e:
        print(f"获取音标失败 ({word}): {str(e)}")
    return ""

def get_example(word):
    """获取单词的例句"""
    # 首先尝试从 Cambridge Dictionary 获取例句
    try:
        soup = get_cambridge_data(word)
        if soup:
            example = soup.find('span', class_='eg')
            if example:
                return example.text.strip()
    except Exception as e:
        print(f"从 Cambridge 获取例句失败 ({word}): {str(e)}")

    # 如果 Cambridge Dictionary 没有例句，返回一个基本例句
    return f"This is an example sentence using the word '{word}'."

def get_definition(word):
    """获取单词的定义"""
    try:
        soup = get_cambridge_data(word)
        if soup:
            def_block = soup.find('div', class_='def')
            if def_block:
                return def_block.text.strip()
    except Exception as e:
        print(f"获取定义失败 ({word}): {str(e)}")
    return ""

def get_synonyms_antonyms(word):
    """获取单词的同义词和反义词"""
    try:
        soup = get_cambridge_data(word)
        synonyms = set()
        antonyms = set()
        
        if soup:
            # 查找同义词
            syn_blocks = soup.find_all('div', class_='synonym')
            for block in syn_blocks:
                words = block.find_all('a')
                for w in words:
                    if w.text.strip() != word:
                        synonyms.add(w.text.strip())
            
            # 由于反义词较难获取，我们可以根据常见的词形变化推测
            if word.endswith('ful'):
                antonyms.add(word.replace('ful', 'less'))
            elif word.endswith('less'):
                antonyms.add(word.replace('less', 'ful'))
            
    except Exception as e:
        print(f"获取同义词反义词失败 ({word}): {str(e)}")
    
    return list(synonyms)[:3], list(antonyms)[:3]

def process_word(word_info):
    """处理单个单词的所有信息"""
    try:
        word, pos, translation = word_info
        
        # 获取所有需要的信息
        phonetic = get_phonetic(word) or "未知音标"
        example = get_example(word)
        definition = get_definition(word) or f"Definition of {word}"
        synonyms, antonyms = get_synonyms_antonyms(word)
        
        # 转换词性格式
        pos_mapping = {
            'v.': 'verb',
            'n.': 'noun',
            'adj.': 'adjective',
            'adv.': 'adverb',
            'prep.': 'preposition',
            'conj.': 'conjunction',
            'pron.': 'pronoun',
            'det.': 'determiner',
            'num.': 'numeral'
        }
        
        # 处理可能有多个词性的情况
        pos_parts = [p.strip() for p in pos.split(',')]
        full_pos_parts = []
        for p in pos_parts:
            p = p.strip()
            if p in pos_mapping:
                full_pos_parts.append(pos_mapping[p])
            else:
                full_pos_parts.append(p)
        
        full_pos = ', '.join(full_pos_parts)
        
        # 如果没有获取到翻译，使用原始翻译
        if not translation.strip():
            translation = translate_word(word) or "需要翻译"
        
        return {
            'word': word,
            'phonetic': phonetic,
            'translation': translation,
            'level': 'B1',  # 默认级别
            'example': example,
            'partOfSpeech': full_pos,
            'definition': definition,
            'synonyms': ','.join(synonyms) if synonyms else '',
            'antonyms': ','.join(antonyms) if antonyms else ''
        }
    except Exception as e:
        print(f"处理单词失败 ({word_info}): {str(e)}")
        # 返回一个基本的记录，避免程序中断
        return {
            'word': word_info[0] if isinstance(word_info, tuple) else str(word_info),
            'phonetic': '未知音标',
            'translation': '需要翻译',
            'level': 'B1',
            'example': '示例句子',
            'partOfSpeech': 'unknown',
            'definition': '需要定义',
            'synonyms': '',
            'antonyms': ''
        }

def process_pdf(input_pdf, output_csv, cache_file=None, batch_size=1000):
    """处理PDF文件并生成CSV文件，每批处理batch_size个单词"""
    print("开始处理PDF文件...")
    
    try:
        with pdfplumber.open(input_pdf) as pdf:
            print(f"成功打开PDF文件: {input_pdf}")
            
            # 收集所有页面的文本
            all_text = ""
            total_pages = len(pdf.pages)
            for page_num, page in enumerate(pdf.pages):
                print(f"正在处理第 {page_num + 1}/{total_pages} 页...")
                all_text += page.extract_text() + "\n"
            
            # 使用正则表达式提取单词信息
            pattern = r'(\w+)\s+([a-z]+\.\s*(?:,\s*[a-z]+\.\s*)*)\s+([^a-zA-Z\n]+)'
            matches = re.findall(pattern, all_text, re.MULTILINE)
            
            total_words = len(matches)
            print(f"找到 {total_words} 个单词")
            
            # 分批处理
            for batch_start in range(0, total_words, batch_size):
                batch_end = min(batch_start + batch_size, total_words)
                current_batch = matches[batch_start:batch_end]
                print(f"\n开始处理第 {batch_start + 1} 到 {batch_end} 个单词...")
                
                # 处理当前批次的单词
                batch_results = []
                with concurrent.futures.ThreadPoolExecutor(max_workers=8) as executor:
                    futures = []
                    for match in current_batch:
                        futures.append(executor.submit(process_word, match))
                        time.sleep(0.3)
                    
                    # 收集当前批次的结果
                    for i, future in enumerate(concurrent.futures.as_completed(futures)):
                        try:
                            result = future.result()
                            batch_results.append(result)
                            if (i + 1) % 50 == 0:
                                print(f"当前批次进度: {((i + 1) / len(current_batch) * 100):.2f}% ({i + 1}/{len(current_batch)})")
                        except Exception as e:
                            print(f"处理单词时出错: {str(e)}")
                
                # 保存当前批次结果到缓存
                if cache_file:
                    batch_cache_file = f"{cache_file[:-4]}_{batch_start+1}_{batch_end}.pkl"
                    try:
                        with open(batch_cache_file, 'wb') as f:
                            pickle.dump(batch_results, f)
                        print(f"当前批次结果已保存到缓存: {batch_cache_file}")
                    except Exception as e:
                        print(f"保存缓存失败: {str(e)}")
                
                # 将当前批次结果追加到CSV文件
                try:
                    # 如果是第一个批次，创建新文件并写入表头
                    mode = 'w' if batch_start == 0 else 'a'
                    with open(output_csv, mode, newline='', encoding='utf-8') as f:
                        writer = csv.DictWriter(f, fieldnames=[
                            'word', 'phonetic', 'translation', 'level', 'example',
                            'partOfSpeech', 'definition', 'synonyms', 'antonyms'
                        ])
                        if batch_start == 0:  # 只在第一个批次写入表头
                            writer.writeheader()
                        writer.writerows(batch_results)
                    print(f"批次 {batch_start + 1}-{batch_end} 已保存到: {output_csv}")
                except Exception as e:
                    print(f"写入CSV文件失败: {str(e)}")
                
                print(f"已完成总进度: {(batch_end / total_words * 100):.2f}% ({batch_end}/{total_words})")
            
            print("\n所有批次处理完成！")
            
    except Exception as e:
        print(f"处理PDF文件失败: {str(e)}")
        return

# 使用示例
if __name__ == "__main__":
    input_pdf = "translations_20250302.pdf"
    timestamp = time.strftime("%Y%m%d_%H%M%S")
    output_csv = f"oxford3000_{timestamp}.csv"
    cache_file = f"oxford3000_cache_{timestamp}.pkl"
    
    # 每批处理1000个单词
    process_pdf(input_pdf, output_csv, cache_file, batch_size=1000)
 