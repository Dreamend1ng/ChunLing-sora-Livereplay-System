/* eslint-disable */
<!-- AdminDashboard.vue -->
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'  // 新增axios导入

const menuItems = ref([
  { title: '首页', path: '/admin/index' },
  { title: '用户管理', path: '/admin/user' },
  { title: '录播管理', path: '/admin/record' },
  { title: '评论管理', path: '/admin/comment' },
  { title: '服务状态', path: '/admin/service' },
  { title: '关于', path: '/admin/about' }
])

// 新增状态管理
const serviceStatus = ref('')
const loading = ref(false)
const error = ref('')

// 获取服务状态
const fetchServiceStatus = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await axios.get('http://localhost:9150/api/admin/serviceStatus')
    serviceStatus.value = res.data
  } catch (err) {
    error.value = `获取失败：${err.response?.data?.message || err.message}`
  } finally {
    loading.value = false
  }
}

// 复制到剪贴板（移除空值判断）
const copyToClipboard = () => {
  // 直接尝试复制（无内容时会复制空字符串）
  navigator.clipboard.writeText(serviceStatus.value)
    .then(() => alert('已复制到剪贴板'))
    .catch(() => alert('复制失败'))
}

// 导出日志文件（移除空值判断）
const exportLog = () => {
  // 无内容时会导出空文件
  const blob = new Blob([serviceStatus.value], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'service-status.log'
  a.click()
  URL.revokeObjectURL(url)
}

onMounted(fetchServiceStatus)  // 挂载后自动请求
</script>

<template>
  <!-- 顶部导航栏（保持不变） -->
  <nav>
    <div class="logo"></div>
    <h3 class="title">椿忆·时空匣 - 管理后台</h3>
    <div class="nav-links">
      <router-link class="nav-link" to="/admin/index">控制台</router-link>
      <router-link class="nav-link" to="/">返回前台</router-link>
    </div>
  </nav>

  <!-- 主内容区（保持不变） -->
  <div class="admin-container">
    <!-- 侧边栏（保持不变） -->
    <aside class="sidebar">
      <div class="sidebar-menu">
        <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="menu-item"
            active-class="active"
        >
          {{ item.title }}
        </router-link>
      </div>
    </aside>

    <!-- 内容区域（关键修改） -->
    <main class="content">
      <div class="status-container">
        <!-- 操作按钮组（移除 disabled 绑定） -->
        <div class="button-group">
          <button 
            class="btn" 
            @click="copyToClipboard"
          >
            {{ loading ? '加载中...' : '复制内容' }}
          </button>
          <button 
            class="btn" 
            @click="exportLog"
          >
            导出日志
          </button>
        </div>

        <!-- 状态显示区域 -->
        <div class="status-textarea">
          <pre v-if="error" class="error-text">{{ error }}</pre>
          <pre v-else-if="loading">正在获取服务状态...</pre>
          <pre v-else>{{ serviceStatus }}</pre>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Microsoft YaHei', sans-serif;
}

nav {
  display: flex;
  align-items: center;
  padding: 1rem 2rem;
  background: rgb(219, 102, 119);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: fixed;
  width: 100%;
  z-index: 1000;
}

.logo {
  width: 40px;
  height: 40px;
  margin-right: 15px;
  background: url('../../assets/cl-logo.jpeg') center/contain no-repeat;
}

.title {
  font-size: 20px;
  color: #ffffff;
  font-weight: 500;
}

.nav-links {
  margin-left: auto;
  display: flex;
  gap: 2rem;
}

.nav-link {
  color: #ffffff;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.3s;
}

.nav-link:hover {
  color: #ffec99;
}

.admin-container {
  display: flex;
  min-height: 100vh;
  padding-top: 64px;
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
}

.sidebar {
  width: 240px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  position: fixed;
  height: calc(100vh - 64px);
}

.sidebar-menu {
  padding: 20px 0;
}

.menu-item {
  display: block;
  padding: 15px 30px;
  color: #666;
  text-decoration: none;
  transition: all 0.3s;
  position: relative;
}

.menu-item:hover {
  background: rgba(219, 102, 119, 0.1);
  color: #db6677;
}

.menu-item.active {
  background: rgba(219, 102, 119, 0.15);
  color: #db6677;
  font-weight: 500;
}

.menu-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 4px;
  background: #db6677;
}

.content {
  flex: 1;
  margin-left: 240px;
  padding: 30px;
  min-height: calc(100vh - 64px);
}

@media (max-width: 768px) {
  .sidebar {
    width: 180px;
  }

  .content {
    margin-left: 180px;
    padding: 20px;
  }

  .menu-item {
    padding: 12px 20px;
    font-size: 14px;
  }

  nav {
    padding: 1rem;
  }

  .title {
    font-size: 16px;
  }

  .nav-link {
    font-size: 14px;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 新增功能样式 */
.status-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.button-group {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

.btn {
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  background: #db6677;
  color: white;
  cursor: pointer;
  transition: background 0.3s;
  font-size: 14px;
}

.btn:disabled {
  background: #ddd;
  cursor: not-allowed;
}

.btn:hover:not(:disabled) {
  background: #c55a6a;
}

.status-textarea {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  overflow: auto;
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap; /* 长文本自动换行 */
}

.error-text {
  color: #ff4444;
}
</style>