/* eslint-disable */
<!-- AdminDashboard.vue -->
<script setup>
import { ref, onMounted, computed } from 'vue'  // 新增computed导入
import axios from 'axios'

// 原有菜单数据保留
const menuItems = ref([
  { title: '首页', path: '/admin/index' },
  { title: '用户管理', path: '/admin/user' },
  { title: '录播管理', path: '/admin/record' },
  { title: '评论管理', path: '/admin/comment' },
  { title: '服务状态', path: '/admin/service' },
  { title: '关于', path: '/admin/about' }
])

// 新增用户管理相关数据
const users = ref([])
const showChangePwdDialog = ref(false)  // 控制密码修改弹窗显示
const currentUserId = ref('')          // 当前操作的用户ID
const newPassword = ref('')            // 新密码
const confirmPassword = ref('')        // 确认新密码

// 获取所有用户数据（关键修改）
const getAllUsers = async () => {
  try {
    const res = await axios.get('http://localhost:9150/api/user/getAll')
    // 修改：将分隔符从', '改为','（根据实际接口返回格式调整）
    users.value = res.data.map(str => {
      const parts = str.split(',').map(part => part.trim())  // 关键修改点
      return {
        id: parts[0].split(': ')[1],
        username: parts[1].split(': ')[1],
        mail: parts[2].split(': ')[1]
      }
    })
    console.log('当前用户数据：', users.value)  // 验证解析后的数据
  } catch (err) {
    alert(`获取用户失败：${err.response?.data || err.message}`)
  }
  console.log(totalPages.value)
}

// 修改密码
const handleChangePassword = async () => {
  if (newPassword.value !== confirmPassword.value) {
    alert('两次输入的密码不一致')
    return
  }
  
  try {
    await axios.post('http://localhost:9150/api/user/changePassword', {
      userId: currentUserId.value,
      newPassword: newPassword.value
    })
    alert('密码修改成功')
    showChangePwdDialog.value = false
    getAllUsers()  // 刷新数据
  } catch (err) {
    alert(`修改失败：${err.response?.data || err.message}`)
  }
}

// 删除用户（二次确认）
const handleDeleteUser = async (userId) => {
  if (!confirm('是否确定删除这个小羽毛的账户？')) return
  if (!confirm('再次确认：确定要删除该账户吗？')) return
  
  try {
    await axios.post('http://localhost:9150/api/user/delUser', { userId })
    alert('删除成功')
    getAllUsers()  // 刷新数据
  } catch (err) {
    alert(`删除失败：${err.response?.data || err.message}`)
  }
}

// 权限提升
const handleRoleUpdate = async (userId, username) => {
  if (!confirm('确定提升TA的权限到管理员吗？')) return
  
  try {
    await axios.post('http://localhost:9150/api/user/roleUpdate', {
      username,
      role: 'admin'
    })
    alert('权限提升成功')
    getAllUsers()  // 刷新数据
  } catch (err) {
    alert(`权限修改失败：${err.response?.data || err.message}`)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  getAllUsers()
})

// 新增分页相关状态
const currentPage = ref(1)       // 当前页码（从1开始）
const pageSize = ref(10)         // 每页显示数量
const total = computed(() => users.value.length)  // 总数据量
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))  // 总页数

// 计算当前页显示的数据（关键新增）
const currentUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return users.value.slice(start, end)
})

// 分页操作函数（新增）
const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++
}


</script>

<template>
  <!-- 顶部导航栏（未修改） -->
  <nav>
    <div class="logo"></div>
    <h3 class="title">椿忆·时空匣 - 管理后台</h3>
    <div class="nav-links">
      <router-link class="nav-link" to="/admin/index">控制台</router-link>
      <router-link class="nav-link" to="/">返回前台</router-link>
    </div>
  </nav>

  <!-- 主内容区（关键修改区域） -->
  <div class="admin-container">
    <!-- 侧边栏（未修改） -->
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

    <!-- 内容区域（新增表格和弹窗） -->
    <main class="content">
      <!-- 用户表格（修改v-for数据源） -->
      <div class="user-table-container">
        <table class="user-table">
          <thead>
            <tr>
              <th>用户ID</th>
              <th>用户名</th>
              <th>邮箱</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <!-- 修改为遍历currentUsers -->
            <tr v-for="user in currentUsers" :key="user.id">  <!-- 关键修改：users → currentUsers -->
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.mail }}</td>
              <td>
                <!-- 修改密码按钮 -->
                <button 
                  class="op-btn" 
                  @click="() => {
                    showChangePwdDialog = true;
                    currentUserId = user.id;
                    newPassword = '';
                    confirmPassword = '';
                  }"
                >
                  修改密码
                </button>
                <!-- 删除账户按钮 -->
                <button class="op-btn" @click="handleDeleteUser(user.id)">删除账户</button>
                <!-- 权限提升按钮 -->
                <button class="op-btn" @click="handleRoleUpdate(user.id, user.username)">权限提升</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 新增分页按钮区域（补充事件绑定） -->
      <div class="pagination-container" v-if="totalPages > 1">  <!-- 修复：分页按钮显示条件 -->
        <button 
          class="op-btn" 
          @click="prevPage"  
          :disabled="currentPage === 1"  
        >
          上一页
        </button>
        
        <span class="page-info">
          第 {{ currentPage }} 页 / 共 {{ totalPages }} 页  <!-- 关键修改：totalPages.value → totalPages -->
        </span>
        
        <button 
          class="op-btn" 
          @click="nextPage"
          :disabled="currentPage === totalPages"  
        >
          下一页
        </button>
      </div>

      <!-- 密码修改弹窗 -->
      <div v-if="showChangePwdDialog" class="dialog-mask">
        <div class="dialog-content">
          <h3>修改密码</h3>
          <!-- 新密码输入 -->
          <div class="input-group">
            <input 
              type="password" 
              v-model="newPassword" 
              placeholder="新密码"
              required
            >
          </div>
          <!-- 确认密码输入 -->
          <div class="input-group">
            <input 
              type="password" 
              v-model="confirmPassword" 
              placeholder="确认新密码"
              required
            >
          </div>
          <!-- 弹窗操作按钮 -->
          <div class="dialog-btns">
            <button @click="handleChangePassword">确认修改</button>
            <button @click="showChangePwdDialog = false">取消</button>
          </div>
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

/* 新增表格样式 */
.user-table-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.user-table th, .user-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.user-table th {
  background: #f8f9fa;
  color: #333;
}

.user-table tr:hover {
  background: #f9f9f9;
}

/* 操作按钮样式 */
.op-btn {
  padding: 6px 12px;
  margin: 0 3px;
  background: #db6677;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.op-btn:hover {
  background: #bf5465;
}

/* 弹窗样式 */
.dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1001;
  display: flex;
  justify-content: center;
  align-items: center;
}

.dialog-content {
  background: white;
  padding: 30px;
  border-radius: 10px;
  min-width: 350px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}

.dialog-content h3 {
  color: #db6677;
  margin-bottom: 20px;
}

.dialog-btns {
  display: flex;
  gap: 15px;
  margin-top: 25px;
}

.dialog-btns button {
  flex: 1;
  padding: 10px;
}

/* 新增分页样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  justify-content: center;
}

.page-info {
  color: #666;
  font-size: 14px;
}

/* 禁用状态按钮样式 */
.op-btn:disabled {
  background: #ddd;
  cursor: not-allowed;
}
</style>
