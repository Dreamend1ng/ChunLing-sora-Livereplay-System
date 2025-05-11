/* eslint-disable */
<!-- AdminDashboard.vue -->
<template>
  <!-- 顶部导航栏 -->
  <nav>
    <div class="logo"></div>
    <h3 class="title">椿忆·时空匣 - 管理后台</h3>
    <div class="nav-links">
      <router-link class="nav-link" to="/admin/index">控制台</router-link>
      <router-link class="nav-link" to="/">返回前台</router-link>
    </div>
  </nav>
  <!-- 主内容区 -->
  <div class="admin-container">
    <!-- 侧边栏 -->
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

    <!-- 内容区域 -->
    <main class="content">
      <!-- 使用动态组件切换 -->
      <component :is="currentComponent" />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const menuItems = ref([
  { title: '首页', path: '/admin/index' },
  { title: '用户管理', path: '/admin/user' },
  { title: '录播管理', path: '/admin/record' },
  { title: '评论管理', path: '/admin/comment' },
  { title: '服务状态', path: '/admin/service' },
  { title: '关于', path: '/admin/about' }
])
</script>


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
</style>