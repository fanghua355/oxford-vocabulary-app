<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted, onUnmounted } from 'vue'

const router = useRouter()
const isMobile = ref(false)

// 检测设备是否为移动设备
const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

const goToWordList = () => {
  router.push('/word-list')
}

const goToStudyPlan = () => {
  router.push('/study-plan')
}

// 监听窗口大小变化
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<template>
  <div class="home-container">
    <el-row :gutter="isMobile ? 10 : 20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <h1>欢迎使用牛津3000核心词汇学习应用</h1>
            <p>这是一款专为成人英语学习者设计的词汇学习工具，帮助您高效掌握牛津3000核心词汇。</p>
            <div class="feature-list">
              <h2>主要功能：</h2>
              <ul>
                <li>包含牛津3000核心词汇，覆盖日常交流所需的基础词汇</li>
                <li>每个单词配有标准发音，帮助您掌握正确的发音</li>
                <li>个性化学习计划，根据您的学习进度调整</li>
                <li>科学的复习机制，提高记忆效率</li>
              </ul>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="isMobile ? 10 : 20" class="action-row">
      <el-col :xs="24" :sm="12" class="action-col">
        <el-card class="action-card">
          <div class="action-content">
            <h2>浏览词汇列表</h2>
            <p>查看所有牛津3000核心词汇，按字母顺序或难度级别排序。</p>
            <el-button type="primary" @click="goToWordList">开始浏览</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" class="action-col">
        <el-card class="action-card">
          <div class="action-content">
            <h2>制定学习计划</h2>
            <p>根据您的学习目标和时间安排，制定个性化的学习计划。</p>
            <el-button type="success" @click="goToStudyPlan">开始计划</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="isMobile ? 10 : 20">
      <el-col :span="24">
        <el-card class="tips-card">
          <div class="tips-content">
            <h2>学习小贴士</h2>
            <p>每天学习15-20个新单词是比较理想的学习量，配合定期复习可以达到最佳效果。</p>
            <p>使用单词发音功能，跟读练习可以帮助您更好地记忆单词。</p>
            <p>将单词应用到实际语境中，能够加深记忆并提高实际应用能力。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 10px;
}

.welcome-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.welcome-content {
  padding: 10px;
}

.welcome-content h1 {
  color: #409EFF;
  margin-bottom: 20px;
}

.feature-list {
  margin-top: 20px;
}

.feature-list ul {
  padding-left: 20px;
}

.feature-list li {
  margin-bottom: 10px;
}

.action-row {
  margin-bottom: 20px;
}

.action-col {
  margin-bottom: 15px;
}

.action-card {
  height: 100%;
  border-radius: 8px;
  transition: transform 0.3s;
}

.action-card:hover {
  transform: translateY(-5px);
}

.action-content {
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.action-content h2 {
  color: #409EFF;
  margin-bottom: 15px;
}

.action-content p {
  margin-bottom: 20px;
}

.tips-card {
  border-radius: 8px;
}

.tips-content {
  padding: 10px;
}

.tips-content h2 {
  color: #409EFF;
  margin-bottom: 15px;
}

.tips-content p {
  margin-bottom: 10px;
}

/* 移动端适配样式 */
@media screen and (max-width: 768px) {
  .welcome-content h1 {
    font-size: 1.5rem;
    margin-bottom: 15px;
  }
  
  .welcome-content p {
    font-size: 0.9rem;
  }
  
  .feature-list h2 {
    font-size: 1.2rem;
  }
  
  .feature-list li {
    font-size: 0.9rem;
    margin-bottom: 8px;
  }
  
  .action-content h2 {
    font-size: 1.2rem;
    margin-bottom: 10px;
  }
  
  .action-content p {
    font-size: 0.9rem;
    margin-bottom: 15px;
  }
  
  .tips-content h2 {
    font-size: 1.2rem;
  }
  
  .tips-content p {
    font-size: 0.9rem;
  }
}
</style> 