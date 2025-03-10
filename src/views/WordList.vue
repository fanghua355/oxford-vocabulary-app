<script setup>
import { ref, onMounted, computed, watch, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { CaretRight, Sort } from '@element-plus/icons-vue'
import wordApi from '../api/wordApi'

const router = useRouter()
const store = useStore()

// 从 store 获取状态，如果不存在则使用默认值
const getStoreState = (key, defaultValue) => {
  return store.state.wordListState?.[key] ?? defaultValue
}

const loading = computed(() => store.state.loading)
const searchQuery = ref(getStoreState('searchQuery', ''))
const currentPage = ref(getStoreState('currentPage', 1))
const pageSize = ref(getStoreState('pageSize', 20))
const filterLevel = ref(getStoreState('filterLevel', ''))
const sortOrder = ref(getStoreState('sortOrder', 'asc'))
const isMobile = ref(false)

// 检测设备是否为移动设备
const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
  // 在移动设备上减少每页显示的数量，但不重置页码
  const newPageSize = isMobile.value ? 10 : 20
  if (pageSize.value !== newPageSize) {
    pageSize.value = newPageSize
  }
}

// 从Vuex获取单词列表
const words = computed(() => store.state.words)

// 过滤和排序后的单词列表
const filteredWords = computed(() => {
  let result = [...words.value]
  
  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(word => 
      word.word.toLowerCase().includes(query) || 
      word.translation.toLowerCase().includes(query)
    )
  }
  
  // 级别过滤
  if (filterLevel.value) {
    result = result.filter(word => word.level === filterLevel.value)
  }
  
  // 排序
  result.sort((a, b) => {
    if (sortOrder.value === 'asc') {
      return a.word.localeCompare(b.word)
    } else {
      return b.word.localeCompare(a.word)
    }
  })
  
  return result
})

// 分页后的单词列表
const paginatedWords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredWords.value.slice(start, end)
})

// 总页数
const totalPages = computed(() => {
  return Math.ceil(filteredWords.value.length / pageSize.value)
})

// 监听状态变化并保存到 store
watch([currentPage, pageSize, searchQuery, filterLevel, sortOrder], () => {
  store.commit('setWordListState', {
    currentPage: currentPage.value,
    pageSize: pageSize.value,
    searchQuery: searchQuery.value,
    filterLevel: filterLevel.value,
    sortOrder: sortOrder.value
  })
})

// 查看单词详情
const viewWordDetail = (wordId) => {
  // 保存当前状态到 localStorage，避免路由切换导致的状态丢失
  localStorage.setItem('lastPage', currentPage.value.toString())
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

// 加载单词数据
const loadWords = async (force = false) => {
  // 如果已经有数据且不是强制刷新，不需要重新加载
  if (words.value.length > 0 && !force) {
    return
  }
  
  store.commit('setLoading', true)
  try {
    const response = await wordApi.getWords()
    store.commit('setWords', response.data)
  } catch (error) {
    console.error('加载单词失败:', error)
    ElMessage({
      message: '加载单词列表失败，请稍后重试',
      type: 'error'
    })
  } finally {
    store.commit('setLoading', false)
  }
}

// 搜索单词
const searchWordsFromApi = async () => {
  if (!searchQuery.value) {
    // 如果搜索词为空且已有数据，不需要重新加载
    if (words.value.length > 0) {
      return
    }
    loadWords()
    return
  }
  
  store.commit('setLoading', true)
  try {
    const response = await wordApi.searchWords(searchQuery.value)
    store.commit('setWords', response.data)
    currentPage.value = 1  // 搜索时重置到第一页
  } catch (error) {
    console.error('搜索单词失败:', error)
    ElMessage({
      message: '搜索单词失败，请稍后重试',
      type: 'error'
    })
  } finally {
    store.commit('setLoading', false)
  }
}

// 按级别过滤
const filterByLevel = async () => {
  if (!filterLevel.value) {
    // 如果没有选择级别且已有数据，不需要重新加载
    if (words.value.length > 0) {
      return
    }
    loadWords()
    return
  }
  
  store.commit('setLoading', true)
  try {
    const response = await wordApi.getWordsByLevel(filterLevel.value)
    store.commit('setWords', response.data)
    currentPage.value = 1  // 筛选时重置到第一页
  } catch (error) {
    console.error('按级别过滤单词失败:', error)
    ElMessage({
      message: '按级别过滤单词失败，请稍后重试',
      type: 'error'
    })
  } finally {
    store.commit('setLoading', false)
  }
}

// 重置过滤器
const resetFilters = () => {
  searchQuery.value = ''
  filterLevel.value = ''
  currentPage.value = 1
  if (words.value.length === 0) {
    loadWords()
  }
}

// 刷新单词列表
const refreshWordList = () => {
  store.commit('setWords', [])  // 清空现有数据
  loadWords(true)  // 强制刷新
}

// 监听窗口大小变化
onMounted(() => {
  // 恢复上次的页码
  const lastPage = localStorage.getItem('lastPage')
  if (lastPage) {
    currentPage.value = parseInt(lastPage)
    localStorage.removeItem('lastPage') // 使用后清除
  }
  
  loadWords()
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})

// 监听搜索查询变化
watch(searchQuery, (newVal, oldVal) => {
  if (newVal === '') {
    // 如果搜索词为空，恢复原始数据
    if (words.value.length === 0) {
      loadWords()
    }
  } else {
    // 有搜索词时进行搜索
    searchWordsFromApi()
  }
}, { flush: 'post' })

// 监听级别过滤变化
watch(filterLevel, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    if (newVal === '') {
      // 如果清除了过滤，恢复原始数据
      if (words.value.length === 0) {
        loadWords()
      }
    } else {
      // 有过滤条件时进行过滤
      filterByLevel()
    }
  }
}, { flush: 'post' })

// 处理页码改变
const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 处理每页显示数量改变
const handleSizeChange = (val) => {
  const oldPageSize = pageSize.value
  pageSize.value = val
  
  // 计算新的页码，保持显示的数据范围大致相同
  const oldStart = (currentPage.value - 1) * oldPageSize
  currentPage.value = Math.floor(oldStart / val) + 1
}
</script>

<template>
  <div class="word-list-container">
    <el-card class="filter-card">
      <div class="filter-header">
        <h2>牛津3000核心词汇</h2>
        <p>浏览和学习牛津3000核心词汇，提高您的英语词汇量</p>
      </div>
      
      <div class="filter-options">
        <el-row :gutter="isMobile ? 10 : 20">
          <el-col :xs="20" :sm="20" :md="16" class="filter-col">
            <el-input
              v-model="searchQuery"
              placeholder="搜索单词或翻译"
              clearable
              prefix-icon="el-icon-search"
              @keyup.enter="searchWordsFromApi"
            />
          </el-col>
          <el-col :xs="4" :sm="4" :md="4" class="filter-col sort-col">
            <el-button
              :type="sortOrder === 'asc' ? 'primary' : 'default'"
              circle
              class="sort-button"
              @click="sortOrder = sortOrder === 'asc' ? 'desc' : 'asc'"
              :title="sortOrder === 'asc' ? '当前：A-Z 升序' : '当前：Z-A 降序'"
            >
              <el-icon :class="{ 'sort-desc': sortOrder === 'desc' }">
                <Sort />
              </el-icon>
            </el-button>
          </el-col>
        </el-row>
        <div class="filter-actions">
          <el-button type="primary" plain @click="resetFilters">重置过滤器</el-button>
          <el-button type="success" @click="refreshWordList">刷新列表</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="word-list-card">
      <!-- 桌面版表格 -->
      <el-table
        v-if="!isMobile"
        v-loading="loading"
        :data="paginatedWords"
        style="width: 100%"
        border
        stripe
      >
        <el-table-column prop="word" label="单词" min-width="160">
          <template #default="{ row }">
            <div class="word-cell">
              <span class="word-text">{{ row.word }}</span>
              <el-button
                type="primary"
                circle
                class="pronunciation-button"
                @click.stop="playPronunciation(row.word)"
                title="播放发音"
              >
                <el-icon>
                  <CaretRight />
                </el-icon>
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
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="viewWordDetail(row.id)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 移动版卡片列表 -->
      <div v-else class="mobile-word-list">
        <div v-for="word in paginatedWords" :key="word.id" class="mobile-word-card" @click="viewWordDetail(word.id)">
          <div class="mobile-word-content">
            <div class="word-info">
              <span class="mobile-word-text">{{ word.word }}</span>
              <span class="mobile-word-phonetic">{{ word.phonetic }}</span>
              <span class="mobile-word-translation">{{ word.translation }}</span>
            </div>
            <el-button
              type="primary"
              circle
              class="pronunciation-button"
              @click.stop="playPronunciation(word.word)"
              title="播放发音"
            >
              <el-icon>
                <CaretRight />
              </el-icon>
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 分页控件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredWords.length"
          :pager-count="3"
          layout="prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
:deep(.el-card) {
  width: 100% !important;
  margin: 0 !important;
  box-sizing: border-box !important;
}

:deep(.el-table) {
  width: 100% !important;
  box-sizing: border-box !important;
}

:deep(.el-pagination) {
  width: 100% !important;
  flex-wrap: wrap !important;
  justify-content: center !important;
}

.word-list-container {
  max-width: 100%;
  width: 100%;
  margin: 0 auto;
  padding: 10px;
  box-sizing: border-box;
  overflow-x: hidden;
}

@media screen and (min-width: 769px) {
  .word-list-container {
    max-width: 1200px;
    padding: 20px;
  }
}

@media screen and (max-width: 768px) {
  :deep(.el-card__body) {
    padding: 8px !important;
  }

  :deep(.el-select) {
    width: 100% !important;
  }

  :deep(.el-pagination) {
    padding: 0 !important;
  }

  :deep(.el-pagination .el-pagination__total) {
    display: block !important;
    width: 100% !important;
    text-align: center !important;
    margin-bottom: 8px !important;
  }

  :deep(.el-pagination .el-pagination__sizes) {
    display: none !important;
  }

  .filter-card,
  .word-list-card {
    margin-bottom: 8px;
    border-radius: 0;
  }

  .filter-header {
    margin-bottom: 8px;
    padding: 0 4px;
  }

  .filter-header h2 {
    font-size: 1.1em;
    margin: 0;
  }

  .filter-header p {
    font-size: 0.85em;
    margin: 4px 0 0;
    color: #666;
  }

  .filter-options {
    margin-bottom: 8px;
  }

  .filter-options .el-row {
    margin: 0 !important;
  }

  .filter-col {
    padding: 0 !important;
    margin-bottom: 6px !important;
  }

  .filter-actions {
    flex-wrap: wrap;
    gap: 6px;
    padding: 0 4px;
  }

  .filter-actions .el-button {
    flex: 1;
    min-width: 100px;
    margin: 0 !important;
    padding: 6px 12px !important;
    height: 32px !important;
  }

  .mobile-word-list {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .mobile-word-card {
    padding: 6px 10px;
    margin: 0;
    border: 1px solid #EBEEF5;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;
  }

  .mobile-word-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 6px;
  }

  .word-info {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 6px;
    min-width: 0;
  }

  .mobile-word-text {
    font-size: 0.95em;
    font-weight: bold;
    white-space: nowrap;
  }

  .mobile-word-phonetic {
    color: #909399;
    font-size: 0.8em;
    white-space: nowrap;
  }

  .mobile-word-translation {
    color: #606266;
    font-size: 0.85em;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .pronunciation-button.el-button {
    width: 24px !important;
    height: 24px !important;
    padding: 0 !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    min-height: 24px !important;
    flex-shrink: 0;
  }

  .pronunciation-button.el-button .el-icon {
    width: 14px !important;
    height: 14px !important;
    transform: scale(1.2) !important;
    margin: 0 !important;
  }

  :deep(.el-input) {
    --el-input-height: 32px;
  }

  :deep(.el-select) {
    --el-select-height: 32px;
  }

  :deep(.el-input__wrapper) {
    padding: 0 8px;
  }

  :deep(.el-input__inner) {
    font-size: 0.9em;
  }
}

.filter-card {
  margin-bottom: 20px;
  width: 100%;
  box-sizing: border-box;
}

.filter-header {
  text-align: center;
  margin-bottom: 20px;
}

.filter-header h2 {
  margin: 0;
  color: #409EFF;
}

.filter-header p {
  margin: 10px 0 0;
  color: #606266;
}

.filter-options {
  margin-bottom: 20px;
}

.filter-col {
  margin-bottom: 10px;
}

.filter-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

.word-list-card {
  margin-bottom: 20px;
  width: 100%;
  box-sizing: border-box;
}

.word-cell {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  /* padding-right: 2px; */
}

.word-text {
  font-weight: bold;
  flex: 1;
}

.mobile-word-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.mobile-word-card {
  padding: 6px 10px;
  margin: 0;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.mobile-word-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.mobile-word-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.mobile-word-text {
  font-size: 16px;
  font-weight: bold;
}

.mobile-word-content {
  color: #606266;
}

.mobile-word-phonetic {
  margin: 5px 0;
  color: #909399;
}

.mobile-word-translation {
  margin: 0;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.word-cell .pronunciation-button.el-button {
  width: 18px !important;
  height: 18px !important;
  padding: 0 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  min-height: 18px !important;
}

.word-cell .pronunciation-button.el-button .el-icon {
  width: 12px !important;
  height: 12px !important;
  transform: scale(1.5) !important;
  margin: 0 !important;
}

.sort-col {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.sort-button {
  width: 32px !important;
  height: 32px !important;
  padding: 0 !important;
}

.sort-button .el-icon {
  transition: transform 0.3s;
}

.sort-button .sort-desc {
  transform: rotate(180deg);
}
</style> 