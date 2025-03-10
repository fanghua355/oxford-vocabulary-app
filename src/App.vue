<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Menu } from '@element-plus/icons-vue'

const router = useRouter()
const activeIndex = ref('1')
const isMobile = ref(false)
const drawerVisible = ref(false)

// 检测设备是否为移动设备
const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

// 处理菜单选择
const handleSelect = (key) => {
  switch(key) {
    case '1':
      router.push('/')
      break
    case '2':
      router.push('/word-list')
      break
    case '3':
      router.push('/study-plan')
      break
  }
  // 在移动端选择菜单项后关闭抽屉
  if (isMobile.value) {
    drawerVisible.value = false
  }
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
  <div class="app-container">
    <el-container>
      <!-- 桌面版头部 -->
      <el-header v-if="!isMobile">
        <div class="header-content">
          <div class="logo">
            <h1>牛津词汇学习</h1>
          </div>
          <el-menu
            :default-active="activeIndex"
            class="el-menu-demo"
            mode="horizontal"
            @select="handleSelect"
          >
            <el-menu-item index="1">首页</el-menu-item>
            <el-menu-item index="2">词汇列表</el-menu-item>
            <el-menu-item index="3">学习计划</el-menu-item>
          </el-menu>
        </div>
      </el-header>
      
      <!-- 移动版头部 -->
      <el-header v-else>
        <div class="mobile-header">
          <div class="logo">
            <h1>牛津词汇学习</h1>
          </div>
          <el-button @click="drawerVisible = true" type="primary" circle>
            <el-icon><Menu /></el-icon>
          </el-button>
        </div>
      </el-header>
      
      <!-- 移动端侧边菜单 -->
      <el-drawer
        v-model="drawerVisible"
        title="菜单"
        direction="rtl"
        size="70%"
        v-if="isMobile"
      >
        <el-menu
          :default-active="activeIndex"
          @select="handleSelect"
        >
          <el-menu-item index="1">首页</el-menu-item>
          <el-menu-item index="2">词汇列表</el-menu-item>
          <el-menu-item index="3">学习计划</el-menu-item>
        </el-menu>
      </el-drawer>
      
      <el-main>
        <router-view />
      </el-main>
      
      <el-footer>
        <p>© 2024 牛津3000核心词汇学习应用 | 为成人英语学习者设计</p>
      </el-footer>
    </el-container>
  </div>
</template>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

.app-container {
  min-height: 100vh;
}

.el-header {
  background-color: #fff;
  color: #333;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
  padding: 0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.mobile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 15px;
}

.logo h1 {
  margin: 0;
  font-size: 24px;
  color: #409EFF;
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
  min-height: calc(100vh - 120px);
}

.el-footer {
  background-color: #545c64;
  color: #fff;
  text-align: center;
  line-height: 60px;
  padding: 0;
}

/* 移动端适配样式 */
@media screen and (max-width: 768px) {
  .logo h1 {
    font-size: 20px;
  }
  
  .el-main {
    padding: 15px;
  }
  
  .el-footer {
    line-height: 40px;
    font-size: 12px;
  }
  
  .el-footer p {
    margin: 10px 0;
  }
}
</style>
