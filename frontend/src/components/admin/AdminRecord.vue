/* eslint-disable */
<!-- AdminDashboard.vue -->
<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'  // 新增axios导入

const menuItems = ref([
  { title: '首页', path: '/admin/index' },
  { title: '用户管理', path: '/admin/user' },
  { title: '录播管理', path: '/admin/record' },
  { title: '评论管理', path: '/admin/comment' },
  { title: '服务状态', path: '/admin/service' },
  { title: '关于', path: '/admin/about' }
])



  // 录播管理相关数据
  const replays = ref([])
  const currentPage = ref(1)
  const pageSize = ref(10)
  const totalPages = ref(1)
  const showEditDialog = ref(false)
  const currentReplayId = ref('')
  const editSpecialTurn = ref('')
  const editLiveTitle = ref('')

  // 计算当前页显示的数据
  const displayedReplays = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return replays.value.slice(start, end)
  })

  // 获取所有录播数据
 const getAllReplay = async () => {
  try {
    const res = await axios.get('http://localhost:9150/api/replay/getAllReplay');
    console.log('获取到的录播数据：', res.data);

    replays.value = res.data.map(item => {
      // 检查是否为字符串
      if (typeof item !== 'string') {
        console.error('非预期数据格式，期望字符串但得到：', item);
        return { id: '', livedate: '', specialturn: '', livetitle: '', hidelive: false };
      }

      // 按逗号分割字符串，并去除每个部分的首尾空白
      const parts = item.split(',').map(part => part.trim());

      // 检查分割后的部分是否足够
      if (parts.length < 5) {
        console.error('数据格式不完整，期望至少5个部分但得到：', parts);
        return { id: '', livedate: '', specialturn: '', livetitle: '', hidelive: false };
      }

      // 提取每个字段的值
      const id = parts[0].split(': ')[1] || '';
      const livedate = parts[1].split(': ')[1] || '';
      const specialturn = parts[2].split(': ')[1] || '';
      const livetitle = parts[3].split(': ')[1] || '';
      
      // 特别处理 hidelive 字段
      let hidelive = false;
      if (parts[4].includes(': ')) {
        const hideliveStr = parts[4].split(': ')[1];
        if (hideliveStr === 'true') {
          hidelive = true;
        } else if (hideliveStr === 'false') {
          hidelive = false;
        } else {
          console.warn('hidelive 字段值无效：', hideliveStr);
        }
      } else {
        // 如果没有 ": " 分隔符，直接检查值是否为 "true" 或 "false"
        const hideliveStr = parts[4].trim();
        if (hideliveStr === 'true') {
          hidelive = true;
        } else if (hideliveStr === 'false') {
          hidelive = false;
        } else {
          console.warn('hidelive 字段值无效：', hideliveStr);
        }
      }

      return {
        id,
        livedate,
        specialturn,
        livetitle,
        hidelive
      };
    });

    console.log('当前 replays 数组：', replays.value);
    totalPages.value = Math.ceil(replays.value.length / pageSize.value);
  } catch (err) {
    console.error('获取录播数据失败：', err);
  }
};

  // 处理编辑
  const handleEdit = (id) => {
    currentReplayId.value = id
    const target = replays.value.find(item => item.id === id)
    editSpecialTurn.value = target.specialturn
    editLiveTitle.value = target.livetitle
    showEditDialog.value = true
  }

  // 确认编辑
  const confirmEdit = async () => {
    try {
      const res = await axios.post('http://localhost:9150/api/replay/changeReplay', {
        id: currentReplayId.value,
        specialturn: editSpecialTurn.value,
        livetitle: editLiveTitle.value
      })
      if (res.data === '修改成功') {
        const index = replays.value.findIndex(item => item.id === currentReplayId.value)
        replays.value[index].specialturn = editSpecialTurn.value
        replays.value[index].livetitle = editLiveTitle.value
        showEditDialog.value = false
        alert('编辑成功！')
      }
    } catch (err) {
      console.error('修改录播信息失败：', err)
      alert('编辑失败：' + (err.message || '未知错误'))
    }
  }

  // 处理隐藏
  const handleHide = async (id) => {
    if (!window.confirm('确认隐藏 / 取消隐藏该录播吗？')) return
    try {
      const res = await axios.get(`http://localhost:9150/api/replay/hideReplay/${id}`)
      if (res.data === '隐藏成功') {
        alert('隐藏成功！')
      }
    } catch (err) {
      console.error('隐藏录播失败：', err)
    }
  }

  onMounted(() => {
    getAllReplay()
  })
</script>

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
      <!-- 录播列表 -->
      <div class="record-table-container">
        <table class="record-table">
          <thead>
            <tr>
              <th>视频id</th>
              <th>直播日期</th>
              <th>场次类型</th>
              <th>直播标题</th>
              <th>隐藏状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="replay in displayedReplays" :key="replay.id">
              <td>{{ replay.id }}</td>
              <td>{{ replay.livedate }}</td>
              <td>{{ replay.specialturn }}</td>
              <td>{{ replay.livetitle }}</td>
              <!-- <td>{{ replay.hidelive === 'false' ? '否' : '是' }}</td> -->
              <td>{{ replay.hidelive ? '是' : '否'  }}</td>
              <td>
                <button class="op-btn" @click="handleEdit(replay.id)">编辑</button>
                <button class="op-btn" @click="handleHide(replay.id)">{{ replay.hidelive ? '取消隐藏' : '隐藏' }}</button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 分页组件 -->
        <div class="pagination-container">
          <button class="op-btn" :disabled="currentPage === 1" @click="currentPage--">上一页</button>
          <span class="page-info">第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
          <button class="op-btn" :disabled="currentPage === totalPages" @click="currentPage++">下一页</button>
        </div>
      </div>

      <!-- 编辑弹窗 -->
      <div v-if="showEditDialog" class="dialog-mask">
        <div class="dialog-content">
          <h3>编辑直播信息</h3>
          <div class="input-group">
            <label>场次类型：</label>
            <input v-model="editSpecialTurn" type="text" />
          </div>
          <div class="input-group">
            <label>直播标题：</label>
            <input v-model="editLiveTitle" type="text" />
          </div>
          <div class="dialog-btns">
            <button @click="confirmEdit">确认</button>
            <button @click="showEditDialog = false">取消</button>
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
.record-table-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.record-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.record-table th, .record-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.record-table th {
  background: #f8f9fa;
  color: #333;
}

.record-table tr:hover {
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