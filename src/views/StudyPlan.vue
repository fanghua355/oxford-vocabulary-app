<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()

const loading = ref(false)
const dailyGoal = ref(20)
const studyDays = ref(5)
const selectedLevel = ref(['A1', 'A2'])
const planWords = ref([])
const studyProgress = ref({})
const activeTab = ref('plan')
const isMobile = ref(false)

// 检测设备是否为移动设备
const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

// 模拟数据 - 实际应用中应从API获取
const mockWords = [
  { id: 1, word: 'abandon', phonetic: '/əˈbændən/', translation: '放弃，抛弃', level: 'B1', example: 'He abandoned his car and continued on foot.' },
  { id: 2, word: 'ability', phonetic: '/əˈbɪləti/', translation: '能力，才能', level: 'A2', example: 'She has the ability to explain complex ideas clearly.' },
  { id: 3, word: 'able', phonetic: '/ˈeɪbl/', translation: '能够，有能力的', level: 'A1', example: 'Will you be able to come to the meeting?' },
  { id: 4, word: 'about', phonetic: '/əˈbaʊt/', translation: '关于，大约', level: 'A1', example: 'What is the book about?' },
  { id: 5, word: 'above', phonetic: '/əˈbʌv/', translation: '在...上方，超过', level: 'A1', example: 'The plane flew above the clouds.' },
  // 更多单词...
]

// 模拟学习进度数据
const mockProgress = {
  1: { status: 'learned', date: '2024-03-08' },
  2: { status: 'learning', date: '2024-03-09' },
  3: { status: 'learning', date: '2024-03-09' },
}

// 计算总学习单词数
const totalWords = computed(() => {
  return dailyGoal.value * studyDays.value
})

// 计算已学习的单词数
const learnedWordsCount = computed(() => {
  return Object.values(studyProgress.value).filter(p => p.status === 'learned').length
})

// 计算学习中的单词数
const learningWordsCount = computed(() => {
  return Object.values(studyProgress.value).filter(p => p.status === 'learning').length
})

// 计算学习进度百分比
const progressPercentage = computed(() => {
  if (planWords.value.length === 0) return 0
  return Math.round((learnedWordsCount.value / planWords.value.length) * 100)
})

// 生成学习计划
const generatePlan = () => {
  loading.value = true
  
  try {
    // 实际应用中，这里应该从API获取数据
    // const response = await axios.post('/api/study-plan', {
    //   dailyGoal: dailyGoal.value,
    //   studyDays: studyDays.value,
    //   levels: selectedLevel.value
    // })
    // planWords.value = response.data
    
    // 使用模拟数据
    setTimeout(() => {
      // 根据选择的级别过滤单词
      const filteredWords = mockWords.filter(word => selectedLevel.value.includes(word.level))
      
      // 随机选择指定数量的单词
      const totalNeeded = dailyGoal.value * studyDays.value
      const shuffled = [...filteredWords].sort(() => 0.5 - Math.random())
      planWords.value = shuffled.slice(0, Math.min(totalNeeded, shuffled.length))
      
      loading.value = false
      
      // 显示成功消息
      ElMessage({
        message: '学习计划已生成',
        type: 'success'
      })
    }, 1000)
  } catch (error) {
    console.error('生成学习计划失败:', error)
    loading.value = false
    
    // 显示错误消息
    ElMessage({
      message: '生成学习计划失败',
      type: 'error'
    })
  }
}

// 查看单词详情
const viewWordDetail = (wordId) => {
  router.push(`/word/${wordId}`)
}

// 播放单词发音
const playPronunciation = (word) => {
  // 实际应用中，这里应该播放音频文件
  // 可以使用 Web Speech API 作为临时解决方案
  const utterance = new SpeechSynthesisUtterance(word)
  utterance.lang = 'en-US'
  speechSynthesis.speak(utterance)
}

// 标记单词状态
const markWordStatus = (wordId, status) => {
  // 实际应用中，这里应该将状态保存到服务器
  // await axios.post(`/api/words/${wordId}/status`, { status })
  
  // 更新本地状态
  studyProgress.value = {
    ...studyProgress.value,
    [wordId]: { status, date: new Date().toISOString().split('T')[0] }
  }
  
  // 显示成功消息
  ElMessage({
    message: `单词状态已更新`,
    type: 'success'
  })
}

// 重置学习计划
const resetPlan = () => {
  ElMessageBox.confirm('确定要重置学习计划吗？这将清除所有计划中的单词。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    planWords.value = []
    
    // 显示成功消息
    ElMessage({
      message: '学习计划已重置',
      type: 'success'
    })
  }).catch(() => {
    // 用户取消操作
  })
}

// 加载数据
const loadData = () => {
  // 实际应用中，这里应该从API获取数据
  // const planResponse = await axios.get('/api/study-plan')
  // planWords.value = planResponse.data
  
  // const progressResponse = await axios.get('/api/study-progress')
  // studyProgress.value = progressResponse.data
  
  // 使用模拟数据
  studyProgress.value = mockProgress
}

// 监听窗口大小变化
onMounted(() => {
  loadData()
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<template>
  <div class="study-plan-container">
    <el-tabs v-model="activeTab" class="study-tabs">
      <el-tab-pane label="学习计划" name="plan">
        <el-card class="plan-card">
          <template #header>
            <div class="card-header">
              <h2>制定学习计划</h2>
              <p>根据您的学习目标和时间安排，制定个性化的学习计划</p>
            </div>
          </template>
          
          <el-form :model="{ dailyGoal, studyDays, selectedLevel }" label-position="top">
            <el-row :gutter="isMobile ? 10 : 20">
              <el-col :xs="24" :sm="12">
                <el-form-item label="每日学习单词数">
                  <el-slider
                    v-model="dailyGoal"
                    :min="5"
                    :max="50"
                    :step="5"
                    show-input
                  />
                </el-form-item>
              </el-col>
              
              <el-col :xs="24" :sm="12">
                <el-form-item label="学习天数">
                  <el-slider
                    v-model="studyDays"
                    :min="1"
                    :max="30"
                    :step="1"
                    show-input
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="单词难度级别">
              <el-checkbox-group v-model="selectedLevel" class="level-checkbox-group">
                <el-checkbox label="A1">初级 (A1)</el-checkbox>
                <el-checkbox label="A2">初中级 (A2)</el-checkbox>
                <el-checkbox label="B1">中级 (B1)</el-checkbox>
                <el-checkbox label="B2">中高级 (B2)</el-checkbox>
                <el-checkbox label="C1">高级 (C1)</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            
            <div class="plan-summary">
              <p>总计划单词数: <strong>{{ totalWords }}</strong> 个</p>
            </div>
            
            <div class="form-actions">
              <el-button type="primary" @click="generatePlan" :loading="loading">生成学习计划</el-button>
              <el-button type="danger" plain @click="resetPlan" :disabled="planWords.length === 0">重置计划</el-button>
            </div>
          </el-form>
        </el-card>
        
        <el-card v-if="planWords.length > 0" class="word-list-card">
          <template #header>
            <div class="card-header">
              <h2>您的学习计划</h2>
              <div class="progress-info">
                <el-progress :percentage="progressPercentage" :format="() => `${learnedWordsCount}/${planWords.length}`" />
                <div class="progress-stats">
                  <span>已学习: {{ learnedWordsCount }}</span>
                  <span>学习中: {{ learningWordsCount }}</span>
                  <span>未开始: {{ planWords.length - learnedWordsCount - learningWordsCount }}</span>
                </div>
              </div>
            </div>
          </template>
          
          <!-- 桌面版表格 -->
          <el-table
            v-if="!isMobile"
            :data="planWords"
            style="width: 100%"
            border
            stripe
          >
            <el-table-column prop="word" label="单词" min-width="120">
              <template #default="{ row }">
                <div class="word-cell">
                  <span class="word-text">{{ row.word }}</span>
                  <el-button
                    type="text"
                    icon="el-icon-headset"
                    @click.stop="playPronunciation(row.word)"
                    title="播放发音"
                  >
                    <i class="el-icon-headset"></i>
                  </el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="phonetic" label="音标" min-width="120" />
            <el-table-column prop="translation" label="翻译" min-width="150" />
            <el-table-column prop="level" label="级别" width="80">
              <template #default="{ row }">
                <el-tag :type="row.level === 'A1' ? 'success' : row.level === 'A2' ? 'info' : row.level === 'B1' ? 'warning' : row.level === 'B2' ? 'danger' : 'primary'">
                  {{ row.level }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120">
              <template #default="{ row }">
                <el-tag v-if="studyProgress[row.id]?.status === 'learned'" type="success">已学习</el-tag>
                <el-tag v-else-if="studyProgress[row.id]?.status === 'learning'" type="warning">学习中</el-tag>
                <el-tag v-else type="info">未开始</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button-group>
                  <el-button
                    type="primary"
                    size="small"
                    @click="viewWordDetail(row.id)"
                  >
                    详情
                  </el-button>
                  <el-button
                    type="success"
                    size="small"
                    @click="markWordStatus(row.id, 'learned')"
                    :disabled="studyProgress[row.id]?.status === 'learned'"
                  >
                    标记为已学习
                  </el-button>
                  <el-button
                    type="warning"
                    size="small"
                    @click="markWordStatus(row.id, 'learning')"
                    :disabled="studyProgress[row.id]?.status === 'learning'"
                  >
                    标记为学习中
                  </el-button>
                </el-button-group>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 移动版卡片列表 -->
          <div v-else class="mobile-word-list">
            <el-card v-for="word in planWords" :key="word.id" class="mobile-word-card">
              <div class="mobile-word-header">
                <div class="mobile-word-title">
                  <span class="word-text">{{ word.word }}</span>
                  <span class="word-phonetic">{{ word.phonetic }}</span>
                </div>
                <el-button
                  type="text"
                  icon="el-icon-headset"
                  @click="playPronunciation(word.word)"
                  title="播放发音"
                  class="pronunciation-btn"
                >
                  <i class="el-icon-headset"></i>
                </el-button>
              </div>
              <div class="mobile-word-content">
                <div class="mobile-word-translation">{{ word.translation }}</div>
                <div class="mobile-word-level">
                  <el-tag size="small" :type="word.level === 'A1' ? 'success' : word.level === 'A2' ? 'info' : word.level === 'B1' ? 'warning' : word.level === 'B2' ? 'danger' : 'primary'">
                    {{ word.level }}
                  </el-tag>
                </div>
              </div>
              <div class="mobile-word-status">
                <el-tag v-if="studyProgress[word.id]?.status === 'learned'" type="success" size="small">已学习</el-tag>
                <el-tag v-else-if="studyProgress[word.id]?.status === 'learning'" type="warning" size="small">学习中</el-tag>
                <el-tag v-else type="info" size="small">未开始</el-tag>
              </div>
              <div class="mobile-word-actions">
                <el-button
                  type="primary"
                  size="small"
                  @click="viewWordDetail(word.id)"
                >
                  详情
                </el-button>
                <el-button
                  type="success"
                  size="small"
                  @click="markWordStatus(word.id, 'learned')"
                  :disabled="studyProgress[word.id]?.status === 'learned'"
                >
                  已学习
                </el-button>
                <el-button
                  type="warning"
                  size="small"
                  @click="markWordStatus(word.id, 'learning')"
                  :disabled="studyProgress[word.id]?.status === 'learning'"
                >
                  学习中
                </el-button>
              </div>
            </el-card>
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="学习统计" name="stats">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <h2>学习统计</h2>
              <p>查看您的学习进度和统计数据</p>
            </div>
          </template>
          
          <div class="stats-content">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="8">
                <div class="stat-item">
                  <div class="stat-value">{{ planWords.length }}</div>
                  <div class="stat-label">计划单词总数</div>
                </div>
              </el-col>
              
              <el-col :xs="24" :sm="8">
                <div class="stat-item">
                  <div class="stat-value">{{ learnedWordsCount }}</div>
                  <div class="stat-label">已学习单词数</div>
                </div>
              </el-col>
              
              <el-col :xs="24" :sm="8">
                <div class="stat-item">
                  <div class="stat-value">{{ progressPercentage }}%</div>
                  <div class="stat-label">完成进度</div>
                </div>
              </el-col>
            </el-row>
            
            <div class="progress-chart">
              <h3>学习进度</h3>
              <el-progress :percentage="progressPercentage" :stroke-width="20" />
            </div>
            
            <div class="level-distribution">
              <h3>单词级别分布</h3>
              <div class="level-bars">
                <div v-for="level in ['A1', 'A2', 'B1', 'B2', 'C1']" :key="level" class="level-bar">
                  <div class="level-label">{{ level }}</div>
                  <el-progress
                    :percentage="Math.round((planWords.filter(w => w.level === level).length / planWords.length) * 100) || 0"
                    :stroke-width="15"
                    :color="level === 'A1' ? '#67C23A' : level === 'A2' ? '#909399' : level === 'B1' ? '#E6A23C' : level === 'B2' ? '#F56C6C' : '#409EFF'"
                  />
                </div>
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card class="tips-card">
          <template #header>
            <div class="card-header">
              <h2>学习建议</h2>
            </div>
          </template>
          
          <div class="tips-content">
            <ul class="tips-list">
              <li>每天坚持学习，保持规律的学习习惯</li>
              <li>使用单词发音功能，跟读练习可以帮助您更好地记忆单词</li>
              <li>学习完成后，尝试用学过的单词造句，加深记忆</li>
              <li>定期复习已学习的单词，防止遗忘</li>
              <li>将单词应用到实际语境中，能够加深记忆并提高实际应用能力</li>
            </ul>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.study-plan-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 10px;
}

.study-tabs {
  margin-bottom: 20px;
}

.plan-card, .word-list-card, .stats-card, .tips-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.card-header {
  margin-bottom: 20px;
}

.card-header h2 {
  color: #409EFF;
  margin-bottom: 10px;
  margin-top: 0;
}

.card-header p {
  margin: 0;
  color: #606266;
}

.plan-summary {
  margin: 20px 0;
  padding: 10px;
  background-color: #f0f9ff;
  border-radius: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.progress-info {
  margin-top: 15px;
}

.progress-stats {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  color: #606266;
}

.word-cell {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.word-text {
  font-weight: bold;
}

/* 移动端卡片样式 */
.mobile-word-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.mobile-word-card {
  border-radius: 8px;
  transition: transform 0.2s;
}

.mobile-word-card:hover {
  transform: translateY(-2px);
}

.mobile-word-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.mobile-word-title {
  display: flex;
  flex-direction: column;
}

.mobile-word-title .word-text {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.mobile-word-title .word-phonetic {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.mobile-word-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.mobile-word-translation {
  font-size: 16px;
}

.mobile-word-status {
  margin-bottom: 10px;
}

.mobile-word-actions {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}

.pronunciation-btn {
  font-size: 20px;
}

/* 移动端适配样式 */
@media screen and (max-width: 768px) {
  .card-header h2 {
    font-size: 1.5rem;
  }
  
  .card-header p {
    font-size: 0.9rem;
  }
  
  .progress-stats {
    flex-direction: column;
    gap: 5px;
  }
  
  .level-checkbox-group {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
    margin-left: 0 !important;
    margin-bottom: 10px;
  }
}
</style> 