<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { CaretRight } from '@element-plus/icons-vue'
import wordApi from '../api/wordApi'

const route = useRoute()
const router = useRouter()
const store = useStore()

const wordId = ref(parseInt(route.params.id))
const loading = ref(false)
const word = ref(null)
const relatedWords = ref([])
const userId = ref(1) // 模拟用户ID，实际应用中应从用户认证系统获取

// 加载单词数据
const loadWordData = async () => {
  loading.value = true
  try {
    // 获取单词详情
    const wordResponse = await wordApi.getWordById(wordId.value)
    word.value = wordResponse.data
    
    if (!word.value) {
      ElMessage({
        message: '未找到单词信息',
        type: 'error'
      })
      router.push('/word-list')
      return
    }
    
    // 获取相关单词（同级别的其他单词）
    const levelResponse = await wordApi.getWordsByLevel(word.value.level)
    relatedWords.value = levelResponse.data
      .filter(w => w.id !== wordId.value)
      .slice(0, 3)
  } catch (error) {
    console.error('加载单词数据失败:', error)
    ElMessage({
      message: '加载单词数据失败，请稍后重试',
      type: 'error'
    })
    router.push('/word-list')
  } finally {
    loading.value = false
  }
}

// 播放单词发音
const playPronunciation = (word) => {
  // 实际应用中，这里应该播放音频文件
  // 可以使用 Web Speech API 作为临时解决方案
  const utterance = new SpeechSynthesisUtterance(word)
  utterance.lang = 'en-US'
  speechSynthesis.speak(utterance)
}

// 返回单词列表
const goBack = () => {
  router.push('/word-list')
}

// 查看其他单词详情
const viewOtherWord = (wordId) => {
  router.push(`/word/${wordId}`)
}

// 添加到学习计划
const addToStudyPlan = async () => {
  try {
    await wordApi.updateProgress(userId.value, word.value.id, 'LEARNING')
    
    ElMessage({
      message: `已将 "${word.value.word}" 添加到学习计划`,
      type: 'success'
    })
  } catch (error) {
    console.error('添加到学习计划失败:', error)
    ElMessage({
      message: '添加到学习计划失败，请稍后重试',
      type: 'error'
    })
  }
}

// 标记为已学习
const markAsLearned = async () => {
  try {
    await wordApi.updateProgress(userId.value, word.value.id, 'LEARNED')
    
    ElMessage({
      message: `已将 "${word.value.word}" 标记为已学习`,
      type: 'success'
    })
  } catch (error) {
    console.error('标记为已学习失败:', error)
    ElMessage({
      message: '标记为已学习失败，请稍后重试',
      type: 'error'
    })
  }
}

onMounted(() => {
  loadWordData()
})
</script>

<template>
  <div class="word-detail-container">
    <el-card v-loading="loading" class="word-detail-card">
      <template #header>
        <div class="card-header">
          <el-button icon="el-icon-back" @click="goBack">返回列表</el-button>
          <div class="header-actions">
            <el-button type="primary" @click="addToStudyPlan">添加到学习计划</el-button>
            <el-button type="success" @click="markAsLearned">标记为已学习</el-button>
          </div>
        </div>
      </template>
      
      <div v-if="word" class="word-content">
        <div class="word-header">
          <div class="word-title">
            <h1>{{ word.word }}</h1>
            <el-tag :type="word.level === 'A1' ? 'success' : word.level === 'A2' ? 'info' : word.level === 'B1' ? 'warning' : word.level === 'B2' ? 'danger' : 'primary'">
              {{ word.level }}
            </el-tag>
          </div>
          
          <div class="word-phonetic">
            <span>{{ word.phonetic }}</span>
            <el-button
              type="primary"
              circle
              class="pronunciation-button"
              @click="playPronunciation(word.word)"
              title="播放发音"
            >
              <el-icon class="play-icon"><CaretRight /></el-icon>
            </el-button>
          </div>
        </div>
        
        <el-divider />
        
        <div class="word-details">
          <div class="detail-item">
            <h3>词性</h3>
            <p>{{ word.partOfSpeech }}</p>
          </div>
          
          <div class="detail-item">
            <h3>翻译</h3>
            <p>{{ word.translation }}</p>
          </div>
          
          <div class="detail-item">
            <h3>定义</h3>
            <p>{{ word.definition }}</p>
          </div>
          
          <div class="detail-item">
            <h3>例句</h3>
            <p>{{ word.example }}</p>
          </div>
          
          <div class="detail-item" v-if="word.synonyms && word.synonyms.length > 0">
            <h3>同义词</h3>
            <div class="word-tags">
              <el-tag
                v-for="(synonym, index) in word.synonyms"
                :key="index"
                effect="plain"
                class="word-tag"
              >
                {{ synonym }}
              </el-tag>
            </div>
          </div>
          
          <div class="detail-item" v-if="word.antonyms && word.antonyms.length > 0">
            <h3>反义词</h3>
            <div class="word-tags">
              <el-tag
                v-for="(antonym, index) in word.antonyms"
                :key="index"
                effect="plain"
                type="danger"
                class="word-tag"
              >
                {{ antonym }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
      
      <div v-else-if="!loading" class="no-word-found">
        <el-empty description="未找到单词信息" />
      </div>
    </el-card>
    
    <el-card v-if="relatedWords.length > 0" class="related-words-card">
      <template #header>
        <div class="card-header">
          <h2>相关单词</h2>
        </div>
      </template>
      
      <div class="related-words-list">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="8" v-for="relatedWord in relatedWords" :key="relatedWord.id">
            <div class="related-word-item" @click="viewOtherWord(relatedWord.id)">
              <div class="related-word-header">
                <span class="related-word-text">{{ relatedWord.word }}</span>
                <el-tag size="small" :type="relatedWord.level === 'A1' ? 'success' : relatedWord.level === 'A2' ? 'info' : relatedWord.level === 'B1' ? 'warning' : relatedWord.level === 'B2' ? 'danger' : 'primary'">
                  {{ relatedWord.level }}
                </el-tag>
              </div>
              <p class="related-word-translation">{{ relatedWord.translation }}</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.word-detail-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 0 10px;
  width: 100%;
  box-sizing: border-box;
}

:deep(.el-card) {
  width: 100% !important;
  margin: 0 !important;
  box-sizing: border-box !important;
}

:deep(.el-card__body) {
  width: 100% !important;
  box-sizing: border-box !important;
  overflow-wrap: break-word !important;
}

.word-detail-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}


.header-actions {
  display: flex;
  gap: 10px;
}

.word-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
  width: 100%;
  position: relative;
}

.word-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.word-title h1 {
  margin: 0;
  font-size: 24px;
  color: #409EFF;
  line-height: 1;
}

.word-phonetic {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #606266;
}

.word-phonetic .pronunciation-button.el-button {
  width: 28px !important;
  height: 28px !important;
  padding: 0 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  position: absolute !important;
  right: 0 !important;
}

.word-details {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-item h3 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #409EFF;
  font-size: 16px;
}

.detail-item p {
  margin: 0;
  font-size: 14px;
  line-height: 1.4;
}

.word-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.word-tag {
  cursor: pointer;
}

.related-words-card {
  border-radius: 8px;
}

.related-word-item {
  padding: 12px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 12px;
}

.related-word-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.related-word-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.related-word-text {
  font-weight: bold;
  font-size: 16px;
}

.related-word-translation {
  margin: 0;
  color: #606266;
}

@media screen and (max-width: 768px) {
  .word-detail-container {
    padding: 0;
    max-width: 100%;
  }

  :deep(.el-card) {
    border-radius: 0;
    margin: 0 !important;
  }

  :deep(.el-card__body) {
    padding: 8px !important;
  }

  .word-header {
    flex-direction: row;
    align-items: center;
    gap: 6px;
    margin-bottom: 10px;
    padding: 4px 0;
  }

  .word-title {
    gap: 6px;
  }

  .word-title h1 {
    font-size: 16px;
  }

  .word-phonetic {
    font-size: 12px;
    margin-left: 0;
  }

  .header-actions {
    width: 100%;
    gap: 4px;
    padding-left: 10px;
  }

  .header-actions .el-button {
    padding: 4px 8px;
    font-size: 12px;
    flex: 1;
  }

  .detail-item h3 {
    font-size: 13px;
    margin-bottom: 4px;
  }

  .detail-item p {
    font-size: 12px;
    line-height: 1.3;
  }

  .word-details {
    gap: 10px;
  }

  .word-phonetic .pronunciation-button.el-button {
    width: 24px !important;
    height: 24px !important;
  }

  .word-phonetic .pronunciation-button.el-button .play-icon {
    width: 16px !important;
    height: 16px !important;
    transform: scale(1.5) !important;
  }

  :deep(.el-tag) {
    font-size: 11px !important;
    padding: 0 4px !important;
    height: 20px !important;
    line-height: 20px !important;
  }
}

.word-phonetic .pronunciation-button.el-button {
  width: 28px !important;
  height: 28px !important;
  padding: 0 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.word-phonetic .pronunciation-button.el-button .play-icon {
  width: 20px !important;
  height: 20px !important;
  transform: scale(1.8) !important;
  margin: 0 !important;
}
</style> 