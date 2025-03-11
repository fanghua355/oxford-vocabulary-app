import axios from 'axios';

// 根据当前环境动态设置 API URL
const API_URL = import.meta.env.PROD 
  ? 'http://124.223.76.88/oxford-vocabulary/api'  // 生产环境使用完整URL
  : window.location.hostname === 'localhost' 
    ? 'http://localhost:8080/api'
    : `http://${window.location.hostname}:8080/api`;

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
  exposedHeaders: ['x-total-count']
});

// 添加请求拦截器，用于调试
apiClient.interceptors.request.use(config => {
  console.log('Making request to:', config.url);
  return config;
});

// 添加响应拦截器，用于调试
apiClient.interceptors.response.use(
  response => {
    // 调试信息
    console.log('Response headers:', response.headers)
    console.log('Total count:', response.headers['x-total-count'])
    return response
  },
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
);

export default {
  // 单词相关API
  getWords(page = 0, size = 20, sort = 'asc') {
    return apiClient.get(`/words?page=${page}&size=${size}&sort=${sort}`);
  },
  
  getWordById(id) {
    return apiClient.get(`/words/${id}`);
  },
  
  getWordsByLevel(level, page = 0, size = 20, sort = 'asc') {
    return apiClient.get(`/words/level/${level}?page=${page}&size=${size}&sort=${sort}`);
  },
  
  searchWords(keyword, page = 0, size = 20, sort = 'asc') {
    return apiClient.get(`/words/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}&sort=${sort}`);
  },
  
  // 学习进度相关API
  getUserProgress(userId) {
    return apiClient.get(`/progress/user/${userId}`);
  },
  
  getProgressByUserAndWord(userId, wordId) {
    return apiClient.get(`/progress/user/${userId}/word/${wordId}`);
  },
  
  updateProgress(userId, wordId, status) {
    return apiClient.post(`/progress/user/${userId}/word/${wordId}?status=${status}`);
  },
  
  getUserStatistics(userId) {
    return apiClient.get(`/progress/user/${userId}/statistics`);
  }
}; 