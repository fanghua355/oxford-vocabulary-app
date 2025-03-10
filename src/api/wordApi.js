import axios from 'axios';

// 根据当前环境动态设置 API URL
const API_URL = import.meta.env.PROD 
  ? '/oxford-vocabulary/api'  // 生产环境使用相对路径
  : window.location.hostname === 'localhost' 
    ? 'http://localhost:8080/api'
    : `http://${window.location.hostname}:8080/api`;

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true
});

// 添加请求拦截器，用于调试
apiClient.interceptors.request.use(config => {
  console.log('Making request to:', config.url);
  return config;
});

// 添加响应拦截器，用于调试
apiClient.interceptors.response.use(
  response => {
    console.log('Received response from:', response.config.url, response.status);
    return response;
  },
  error => {
    console.error('API Error:', error.config.url, error.message);
    return Promise.reject(error);
  }
);

export default {
  // 单词相关API
  getWords() {
    return apiClient.get('/words');
  },
  
  getWordById(id) {
    return apiClient.get(`/words/${id}`);
  },
  
  getWordsByLevel(level) {
    return apiClient.get(`/words/level/${level}`);
  },
  
  searchWords(keyword) {
    return apiClient.get(`/words/search?keyword=${encodeURIComponent(keyword)}`);
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