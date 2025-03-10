import axios from 'axios'

// 模拟数据 - 实际应用中应从API获取
const mockWords = [
  { id: 1, word: 'abandon', phonetic: '/əˈbændən/', translation: '放弃，抛弃', level: 'B1', example: 'He abandoned his car and continued on foot.', partOfSpeech: 'verb', definition: 'to leave a place, thing, or person, usually forever', synonyms: ['desert', 'leave', 'forsake'], antonyms: ['keep', 'retain', 'maintain'] },
  { id: 2, word: 'ability', phonetic: '/əˈbɪləti/', translation: '能力，才能', level: 'A2', example: 'She has the ability to explain complex ideas clearly.', partOfSpeech: 'noun', definition: 'the physical or mental power or skill needed to do something', synonyms: ['capability', 'capacity', 'skill'], antonyms: ['inability', 'incapacity', 'weakness'] },
  { id: 3, word: 'able', phonetic: '/ˈeɪbl/', translation: '能够，有能力的', level: 'A1', example: 'Will you be able to come to the meeting?', partOfSpeech: 'adjective', definition: 'having the necessary power, skill, resources, or qualifications to do something', synonyms: ['capable', 'competent', 'skilled'], antonyms: ['unable', 'incapable', 'incompetent'] },
  { id: 4, word: 'about', phonetic: '/əˈbaʊt/', translation: '关于，大约', level: 'A1', example: 'What is the book about?', partOfSpeech: 'preposition', definition: 'on the subject of; concerning', synonyms: ['concerning', 'regarding', 'relating to'], antonyms: [] },
  { id: 5, word: 'above', phonetic: '/əˈbʌv/', translation: '在...上方，超过', level: 'A1', example: 'The plane flew above the clouds.', partOfSpeech: 'preposition', definition: 'in or to a higher place than; over', synonyms: ['over', 'higher than', 'overhead'], antonyms: ['below', 'beneath', 'under'] },
  { id: 6, word: 'abroad', phonetic: '/əˈbrɔːd/', translation: '在国外', level: 'A2', example: 'Have you ever been abroad?', partOfSpeech: 'adverb', definition: 'in or to a foreign country', synonyms: ['overseas', 'internationally'], antonyms: [] },
  { id: 7, word: 'absence', phonetic: '/ˈæbsəns/', translation: '缺席，不在场', level: 'B1', example: 'She explained her absence from the meeting.', partOfSpeech: 'noun', definition: 'the state of being away from a place or person', synonyms: ['non-attendance', 'non-presence'], antonyms: ['presence', 'attendance'] },
  { id: 8, word: 'absolute', phonetic: '/ˈæbsəluːt/', translation: '绝对的，完全的', level: 'B2', example: 'I have absolute confidence in her ability.', partOfSpeech: 'adjective', definition: 'total and complete', synonyms: ['complete', 'total', 'utter'], antonyms: ['partial', 'incomplete', 'limited'] },
  { id: 9, word: 'absolutely', phonetic: '/ˈæbsəluːtli/', translation: '绝对地，完全地', level: 'A2', example: 'I absolutely agree with you.', partOfSpeech: 'adverb', definition: 'completely or totally', synonyms: ['completely', 'totally', 'entirely'], antonyms: ['partially', 'somewhat'] },
  { id: 10, word: 'absorb', phonetic: '/əbˈzɔːrb/', translation: '吸收，吸引', level: 'B1', example: 'Plants absorb water through their roots.', partOfSpeech: 'verb', definition: 'to take in or soak up (energy or a liquid or other substance) by chemical or physical action', synonyms: ['soak up', 'take in', 'draw in'], antonyms: ['emit', 'exude', 'release'] },
  // 更多单词...
]

// 获取所有单词
export const getAllWords = () => {
  return new Promise((resolve) => {
    // 模拟API请求延迟
    setTimeout(() => {
      resolve(mockWords)
    }, 500)
  })
}

// 获取单个单词详情
export const getWordById = (id) => {
  return new Promise((resolve, reject) => {
    // 模拟API请求延迟
    setTimeout(() => {
      const word = mockWords.find(w => w.id === id)
      if (word) {
        resolve(word)
      } else {
        reject(new Error('单词未找到'))
      }
    }, 300)
  })
}

// 获取单词发音
export const getWordPronunciation = (word) => {
  // 实际应用中，这里应该返回音频文件URL
  // 可以使用第三方API，如Google Text-to-Speech API
  return `https://api.example.com/pronunciation/${encodeURIComponent(word)}`
}

// 根据级别获取单词
export const getWordsByLevel = (level) => {
  return new Promise((resolve) => {
    // 模拟API请求延迟
    setTimeout(() => {
      const filteredWords = mockWords.filter(w => w.level === level)
      resolve(filteredWords)
    }, 300)
  })
}

// 搜索单词
export const searchWords = (query) => {
  return new Promise((resolve) => {
    // 模拟API请求延迟
    setTimeout(() => {
      const lowerQuery = query.toLowerCase()
      const filteredWords = mockWords.filter(w => 
        w.word.toLowerCase().includes(lowerQuery) || 
        w.translation.toLowerCase().includes(lowerQuery)
      )
      resolve(filteredWords)
    }, 300)
  })
}

// 获取相关单词（同义词或同级别的其他单词）
export const getRelatedWords = (wordId, limit = 3) => {
  return new Promise((resolve) => {
    // 模拟API请求延迟
    setTimeout(() => {
      const word = mockWords.find(w => w.id === wordId)
      if (!word) {
        resolve([])
        return
      }
      
      const relatedWords = mockWords
        .filter(w => w.id !== wordId && (w.level === word.level || word.synonyms?.includes(w.word)))
        .slice(0, limit)
      
      resolve(relatedWords)
    }, 300)
  })
}

export default {
  getAllWords,
  getWordById,
  getWordPronunciation,
  getWordsByLevel,
  searchWords,
  getRelatedWords
} 