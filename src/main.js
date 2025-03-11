import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createRouter, createWebHistory } from 'vue-router'
import { createStore } from 'vuex'
import './style.css'
import App from './App.vue'
import axios from 'axios'

// 导入路由组件
import Home from './views/Home.vue'
import WordList from './views/WordList.vue'
import WordDetail from './views/WordDetail.vue'
import StudyPlan from './views/StudyPlan.vue'

// 创建路由
const router = createRouter({
  history: createWebHistory('/'),
  routes: [
    { path: '/', component: Home },
    { path: '/word-list', component: WordList },
    { path: '/word/:id', component: WordDetail },
    { path: '/study-plan', component: StudyPlan }
  ]
})

// 模拟数据 - 实际应用中应从API获取
const mockWords = [
  { 
    id: 1, 
    word: 'abandon', 
    phonetic: '/əˈbændən/', 
    translation: '放弃，抛弃', 
    level: 'B1', 
    example: 'He abandoned his car and continued on foot.', 
    partOfSpeech: 'verb', 
    definition: 'to leave a place, thing, or person, usually forever', 
    synonyms: ['desert', 'leave', 'forsake'], 
    antonyms: ['keep', 'retain', 'maintain'] 
  },
  { 
    id: 2, 
    word: 'ability', 
    phonetic: '/əˈbɪləti/', 
    translation: '能力，才能', 
    level: 'A2', 
    example: 'She has the ability to explain complex ideas clearly.', 
    partOfSpeech: 'noun', 
    definition: 'the physical or mental power or skill needed to do something', 
    synonyms: ['capability', 'capacity', 'skill'], 
    antonyms: ['inability', 'incapacity', 'weakness'] 
  },
  { 
    id: 3, 
    word: 'able', 
    phonetic: '/ˈeɪbl/', 
    translation: '能够，有能力的', 
    level: 'A1', 
    example: 'Will you be able to come to the meeting?', 
    partOfSpeech: 'adjective', 
    definition: 'having the necessary power, skill, resources, or qualifications to do something', 
    synonyms: ['capable', 'competent', 'skilled'], 
    antonyms: ['unable', 'incapable', 'incompetent'] 
  },
  { 
    id: 4, 
    word: 'about', 
    phonetic: '/əˈbaʊt/', 
    translation: '关于，大约', 
    level: 'A1', 
    example: 'What is the book about?', 
    partOfSpeech: 'preposition', 
    definition: 'on the subject of; concerning', 
    synonyms: ['concerning', 'regarding', 'relating to'], 
    antonyms: [] 
  },
  { 
    id: 5, 
    word: 'above', 
    phonetic: '/əˈbʌv/', 
    translation: '在...上方，超过', 
    level: 'A1', 
    example: 'The plane flew above the clouds.', 
    partOfSpeech: 'preposition', 
    definition: 'in or to a higher place than; over', 
    synonyms: ['over', 'higher than', 'overhead'], 
    antonyms: ['below', 'beneath', 'under'] 
  },
  { 
    id: 6, 
    word: 'accept', 
    phonetic: '/əkˈsept/', 
    translation: '接受', 
    level: 'A2', 
    example: 'I accept your apology.', 
    partOfSpeech: 'verb', 
    definition: 'to agree to take something', 
    synonyms: ['receive', 'take', 'agree to'], 
    antonyms: ['refuse', 'reject', 'decline'] 
  },
  { 
    id: 7, 
    word: 'accident', 
    phonetic: '/ˈæksɪdənt/', 
    translation: '事故', 
    level: 'A2', 
    example: 'He was injured in a car accident.', 
    partOfSpeech: 'noun', 
    definition: 'an unfortunate event that happens unexpectedly and unintentionally', 
    synonyms: ['mishap', 'incident', 'crash'], 
    antonyms: [] 
  },
  { 
    id: 8, 
    word: 'according to', 
    phonetic: '/əˈkɔːrdɪŋ tuː/', 
    translation: '根据', 
    level: 'B1', 
    example: 'According to the weather forecast, it will rain tomorrow.', 
    partOfSpeech: 'preposition', 
    definition: 'as stated or reported by', 
    synonyms: ['as reported by', 'as stated by'], 
    antonyms: [] 
  },
  { 
    id: 9, 
    word: 'account', 
    phonetic: '/əˈkaʊnt/', 
    translation: '账户', 
    level: 'B1', 
    example: 'I need to open a bank account.', 
    partOfSpeech: 'noun', 
    definition: 'an arrangement with a bank to keep your money there', 
    synonyms: ['bank account', 'record', 'report'], 
    antonyms: [] 
  },
  { 
    id: 10, 
    word: 'achieve', 
    phonetic: '/əˈtʃiːv/', 
    translation: '实现', 
    level: 'B1', 
    example: 'She achieved her goal of becoming a doctor.', 
    partOfSpeech: 'verb', 
    definition: 'to succeed in doing something good or getting the result you wanted', 
    synonyms: ['accomplish', 'attain', 'reach'], 
    antonyms: ['fail', 'lose', 'miss'] 
  },
  { 
    id: 11, 
    word: 'across', 
    phonetic: '/əˈkrɔːs/', 
    translation: '穿过', 
    level: 'A1', 
    example: 'We walked across the bridge.', 
    partOfSpeech: 'preposition', 
    definition: 'from one side to the other', 
    synonyms: ['through', 'over'], 
    antonyms: [] 
  },
  { 
    id: 12, 
    word: 'act', 
    phonetic: '/ækt/', 
    translation: '行动，表演', 
    level: 'A2', 
    example: 'He acted in a movie last year.', 
    partOfSpeech: 'verb', 
    definition: 'to do something or behave in a certain way', 
    synonyms: ['perform', 'behave', 'do'], 
    antonyms: [] 
  },
  { 
    id: 13, 
    word: 'active', 
    phonetic: '/ˈæktɪv/', 
    translation: '活跃的', 
    level: 'A2', 
    example: 'She leads an active lifestyle.', 
    partOfSpeech: 'adjective', 
    definition: 'doing a lot of things, or moving around a lot', 
    synonyms: ['energetic', 'lively', 'dynamic'], 
    antonyms: ['passive', 'inactive', 'lazy'] 
  },
  { 
    id: 14, 
    word: 'activity', 
    phonetic: '/ækˈtɪvəti/', 
    translation: '活动', 
    level: 'A1', 
    example: 'The school offers many after-school activities.', 
    partOfSpeech: 'noun', 
    definition: 'something that you do for enjoyment or a particular purpose', 
    synonyms: ['pursuit', 'pastime', 'hobby'], 
    antonyms: ['inactivity'] 
  },
  { 
    id: 15, 
    word: 'actor', 
    phonetic: '/ˈæktər/', 
    translation: '演员', 
    level: 'A1', 
    example: 'He is a famous actor in Hollywood.', 
    partOfSpeech: 'noun', 
    definition: 'someone who performs in plays, films, etc.', 
    synonyms: ['performer', 'player', 'artist'], 
    antonyms: [] 
  },
  { 
    id: 16, 
    word: 'actress', 
    phonetic: '/ˈæktrəs/', 
    translation: '女演员', 
    level: 'A1', 
    example: 'She is an award-winning actress.', 
    partOfSpeech: 'noun', 
    definition: 'a woman who performs in plays, films, etc.', 
    synonyms: ['performer', 'player', 'artist'], 
    antonyms: [] 
  },
  { 
    id: 17, 
    word: 'actual', 
    phonetic: '/ˈæktʃuəl/', 
    translation: '实际的', 
    level: 'B1', 
    example: 'The actual cost was higher than expected.', 
    partOfSpeech: 'adjective', 
    definition: 'real or existing in fact', 
    synonyms: ['real', 'true', 'factual'], 
    antonyms: ['imaginary', 'unreal', 'fictional'] 
  },
  { 
    id: 18, 
    word: 'actually', 
    phonetic: '/ˈæktʃuəli/', 
    translation: '实际上', 
    level: 'A2', 
    example: 'I thought he was French, but actually he\'s Belgian.', 
    partOfSpeech: 'adverb', 
    definition: 'used to emphasize that something is true', 
    synonyms: ['really', 'in fact', 'indeed'], 
    antonyms: [] 
  },
  { 
    id: 19, 
    word: 'add', 
    phonetic: '/æd/', 
    translation: '添加', 
    level: 'A1', 
    example: 'Add some salt to the soup.', 
    partOfSpeech: 'verb', 
    definition: 'to put something with another thing', 
    synonyms: ['include', 'insert', 'append'], 
    antonyms: ['subtract', 'remove', 'take away'] 
  },
  { 
    id: 20, 
    word: 'address', 
    phonetic: '/əˈdres/', 
    translation: '地址', 
    level: 'A1', 
    example: 'What is your home address?', 
    partOfSpeech: 'noun', 
    definition: 'the details of where someone lives or works', 
    synonyms: ['location', 'residence', 'place'], 
    antonyms: [] 
  }
]

// 创建状态管理
const store = createStore({
  state() {
    return {
      words: [],
      currentWord: null,
      studyProgress: {},
      loading: false,
      userId: 1, // 模拟用户ID，实际应用中应从用户认证系统获取
      wordListState: {
        currentPage: 1,
        pageSize: 20,
        searchQuery: '',
        filterLevel: '',
        sortOrder: 'asc'
      }
    }
  },
  mutations: {
    setWords(state, words) {
      state.words = words
    },
    setCurrentWord(state, word) {
      state.currentWord = word
    },
    updateProgress(state, { wordId, status }) {
      state.studyProgress[wordId] = status
    },
    setLoading(state, status) {
      state.loading = status
    },
    setWordListState(state, payload) {
      state.wordListState = {
        ...state.wordListState,
        ...payload
      }
    }
  },
  actions: {
    // 获取所有单词数据
    fetchWords({ commit }) {
      commit('setLoading', true)
      
      return axios.get('http://localhost:8080/api/words')
        .then(response => {
          commit('setWords', response.data)
          commit('setLoading', false)
        })
        .catch(error => {
          console.error('获取单词数据失败:', error)
          commit('setLoading', false)
          throw error
        })
    },
    
    // 获取单个单词详情
    fetchWordDetail({ commit, state }, wordId) {
      commit('setLoading', true)
      
      // 实际应用中，这里应该从API获取数据
      // return axios.get(`/api/words/${wordId}`)
      //   .then(response => {
      //     commit('setCurrentWord', response.data)
      //     commit('setLoading', false)
      //   })
      //   .catch(error => {
      //     console.error('获取单词详情失败:', error)
      //     commit('setLoading', false)
      //     throw error
      //   })
      
      // 使用模拟数据
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          const word = mockWords.find(w => w.id === wordId)
          if (word) {
            commit('setCurrentWord', word)
            commit('setLoading', false)
            resolve(word)
          } else {
            commit('setLoading', false)
            reject(new Error('未找到单词'))
          }
        }, 300)
      })
    },
    
    // 更新学习进度
    updateWordProgress({ commit }, { wordId, status }) {
      commit('updateProgress', { wordId, status })
      
      // 实际应用中，这里应该将进度保存到服务器
      // return axios.post(`/api/progress/${wordId}`, { status })
      //   .catch(error => {
      //     console.error('更新学习进度失败:', error)
      //     throw error
      //   })
      
      // 模拟成功
      return Promise.resolve()
    }
  }
})

const app = createApp(App)

// 使用插件
app.use(ElementPlus)
app.use(router)
app.use(store)

app.mount('#app')
