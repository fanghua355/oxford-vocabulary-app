import { createStore } from 'vuex'

// 从 localStorage 获取状态或使用默认值
const getInitialState = () => {
  const savedState = localStorage.getItem('wordListState')
  return savedState ? JSON.parse(savedState) : {
    currentPage: 1,
    pageSize: 20,
    searchQuery: '',
    filterLevel: '',
    sortOrder: 'asc'
  }
}

// 从 localStorage 获取单词数据
const getInitialWords = () => {
  const savedWords = localStorage.getItem('words')
  return savedWords ? JSON.parse(savedWords) : []
}

export default createStore({
  state: {
    words: getInitialWords(),
    loading: false,
    wordListState: getInitialState()
  },
  mutations: {
    setWords(state, words) {
      state.words = words
      // 保存到 localStorage
      localStorage.setItem('words', JSON.stringify(words))
    },
    setLoading(state, loading) {
      state.loading = loading
    },
    setWordListState(state, payload) {
      state.wordListState = {
        ...state.wordListState,
        ...payload
      }
      // 保存到 localStorage
      localStorage.setItem('wordListState', JSON.stringify(state.wordListState))
    }
  },
  actions: {
    // 可以添加异步操作
  }
}) 