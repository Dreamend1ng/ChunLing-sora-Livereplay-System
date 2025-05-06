<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import 'video.js/dist/video-js.css'
import videojs from 'video.js'

const replays = ref([])
const loading = ref(true)
const error = ref(null)
//testtesttest
// 从API获取回放数据
const fetchReplays = async (type = 'all') => {
  try {
    let apiUrl = 'http://localhost:9150/api/replay/getAllReplay';
    if (type === '打字场') {
      apiUrl = 'http://localhost:9150/api/replay/getMorningReplay';
    } else if (type === '聊天场') {
      apiUrl = 'http://localhost:9150/api/replay/getEveningReplay';
    }

    const response = await fetch(apiUrl);
    if (!response.ok) throw new Error('获取回放数据失败')
    const data = await response.json()
    console.log('API返回的数据:', data)
    console.log('API返回的数据长度:', data.length)

    if (data.length > 0) {
      console.log('第一个数据项:', data[0])
      console.log('第一个数据项类型:', typeof data[0])
    }

    // 确认每一项都是一个字符串
    if (data.every(item => typeof item === 'string')) {
      replays.value = data.map(item => {
        // 使用正则表达式提取键值对
        const keyValuePairs = item.split(',').map(pair => pair.trim().split(': '))
        const replay = {}
        keyValuePairs.forEach(pair => {
          const [key, value] = pair
          replay[key] = value
        })
        return {
          id: replay['ID'] || '',
          title: replay['LiveTitle'] || '',
          type: replay['SpecialTurn'] || '',
          date: replay['LiveDate'] || '',
          duration: replay['LiveDuration'] || '',
        }
      })
    } else {
      console.error('数据项类型不正确，有些项不是字符串')
    }

    console.log('映射后的 replays:', replays.value)
  } catch (err) {
    error.value = err.message
    console.error('获取回放数据时出错:', err)
  } finally {
    loading.value = false
  }
}



onMounted(() => {
  fetchReplays()
  player = videojs('my-video', {
    controls: true,
    fluid: true,
    responsive: true
  })
})

// 销毁播放器
onBeforeUnmount(() => {
  if (player) {
    player.dispose()
  }
})

// 筛选后的回放列表
const filterType = ref('all')  // 添加这行，定义filterType
const filterDate = ref('')     // 添加这行，定义filterDate
let player = null              // 确保player定义在顶部

// 修改筛选后的回放列表逻辑
const filteredReplays = computed(() => {
  return replays.value.filter(item => {
    const typeMatch = filterType.value === 'all' || item.type === filterType.value
    const dateMatch = !filterDate.value || item.date.includes(filterDate.value) // 修改为includes匹配
    return typeMatch && dateMatch
  })
})

// 修改播放视频函数，增加空值检查
// 修改播放视频函数，改为发送GET请求获取视频流
const playVideo = async (id, title) => {
  if (!player || !id) return;

  try {
    // 显示加载状态
    loading.value = true;
    error.value = null;

    // 发送请求获取视频流
    const response = await fetch(`http://localhost:9150/api/replay/playReplay/${id}`);

    if (!response.ok) throw new Error('获取视频流失败');

    // 创建视频URL
    const videoUrl = `http://localhost:9150/api/replay/playReplay/${id}`;

    // 设置视频源并播放
    player.src({ type: 'video/mp4', src: videoUrl });
    player.play();
    document.getElementById('videoInfo').textContent = title;

  } catch (err) {
    console.error('播放视频时出错:', err);
    error.value = '播放视频失败: ' + err.message;
  } finally {
    loading.value = false;
  }
}

// 添加 comments 定义
const comments = ref([
  { id: 1, user: '小羽毛123', content: '这场打字回放太精彩了！', time: '2024-03-10 14:30' },
  { id: 2, user: '咕咕粉丝', content: '期待下次直播~', time: '2024-03-10 15:00' }
])

const newComment = ref('')

const submitComment = () => {
  if (newComment.value.trim()) {
    comments.value.push({
      id: comments.value.length + 1,
      user: '匿名用户',
      content: newComment.value.trim(),
      time: new Date().toLocaleString()
    })
    newComment.value = ''
  }
}
</script>

<template>
  <nav>
    <div class="logo"></div>
    <h3 class="title">椿忆·时空匣 - 椿翎Sora的录像观看站</h3>
    <div class="nav-links">
      <router-link class="nav-link" to="home">首页</router-link>
      <router-link class="nav-link" to="record">回放直播</router-link>
      <router-link class="nav-link" to="login">小羽毛注册/登录</router-link>
    </div>
  </nav>

  <div class="page">
    <div class="container">
      <div class="content-container">
        <div class="video-container">
          <video id="my-video" class="video-js vjs-big-play-centered">
            <source src="" type="video/mp4">
          </video>
          <div class="video-info" id="videoInfo"></div>
        </div>

        <template class="replay-list">
          <!-- 添加加载状态和错误提示 -->
          <div v-if="loading" class="loading">加载中...</div>
          <div v-else-if="error" class="error">{{ error }}</div>
          <template v-else>
            <div class="replay-header">
              <div class="filter-group">
                <select v-model="filterType" class="soft-filter">
                  <option value="all">全部类型</option>
                  <option value="打字场">打字场</option>
                  <option value="聊天场">聊天场</option>
                </select>
                <input type="date" v-model="filterDate" class="soft-filter">
              </div>
            </div>

            <div
                v-for="item in filteredReplays"
                :key="item.id"
                class="replay-item"
                @click="playVideo(item.id, item.title)"
            >
              <div>{{ item.title }}</div>
              <div class="meta-info">
                {{ item.type === 'typing' ? '打字场' : '聊天场' }} ·
                {{ item.date }} ·
                {{ item.duration }}
              </div>
            </div>
          </template>  <!-- 这是正确的replay-list闭合标签 -->
        </template>
        </div>
      </div>

      <div class="comment-section">
        <h4>羽毛留言板（{{ comments.length }}）</h4>
        <div class="comment-input">
          <textarea
              v-model="newComment"
              placeholder="留下你的爪印吧~ (✧ω✧)"
              rows="3"
          ></textarea>
          <button @click="submitComment" class="comment-btn">咻咻~评论发送！</button><br><br><br>
        </div>
        <div class="comment-list">
          <div
              v-for="comment in comments"
              :key="comment.id"
              class="comment-item"
          >
            <div class="comment-header">
              <span class="comment-user">{{ comment.user }}</span>
              <span class="comment-time">{{ comment.time }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
          </div>
        </div>
      </div>
    </div>

</template>

<style scoped>
nav {
  display: flex;
  align-items: center;
  padding: 1rem 2rem;
  background: rgb(219, 102, 119);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.logo {
  width: 40px;
  height: 40px;
  margin-right: 15px;
  background: url('../assets/cl-logo.jpeg') center/contain no-repeat;
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

.page {
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.content-container {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
  margin-top: 20px;
}

.video-container {
  background-color: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-info {
  margin-top: 10px;
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
  color: #333;
}

/* 回放列表样式 */
.replay-list {
  background-color: rgba(255, 255, 255, 0.9);
  padding: 15px;
  border-radius: 8px;
  max-height: 600px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.replay-header {
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: rgba(255, 255, 255, 0.9);
  padding: 15px;
  margin: -15px -15px 15px -15px;
  border-radius: 8px 8px 0 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.filter-group {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.replay-item {
  padding: 12px;
  background-color: #fff;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.replay-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.meta-info {
  font-size: 0.8em;
  color: #666;
  margin-top: 5px;
}

/* 筛选器样式 */
.soft-filter {
  flex: 1;
  min-width: 150px;
  padding: 8px 15px;
  border: none;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(5px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.soft-filter:hover {
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
}

.soft-filter:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(219, 102, 119, 0.3),
  0 4px 10px rgba(0, 0, 0, 0.12);
}

/* 评论区样式 */
.comment-section {
  margin-top: 30px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.comment-section h4 {
  color: #db6677;
  margin-bottom: 15px;
  font-size: 18px;
}

.comment-input {
  margin-bottom: 20px;
  position: relative;
}

.comment-input textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #fad0c4;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.95);
  resize: vertical;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.comment-input textarea:focus {
  outline: none;
  border-color: #db6677;
  box-shadow: 0 0 8px rgba(219, 102, 119, 0.2);
}

.comment-btn {
  display: inline-block;
  margin-top: 10px;
  padding: 8px 20px;
  background: #db6677;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  float: right;
  clear: both;
}

.comment-btn:hover {
  background: #ff9a9e;
  transform: translateY(-1px);
}

.comment-list {
  max-height: 400px;
  overflow-y: auto;
}

.comment-item {
  padding: 15px;
  margin-bottom: 15px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 0.9em;
}

.comment-user {
  color: #db6677;
  font-weight: 500;
}

.comment-time {
  color: #999;
}

.comment-content {
  color: #666;
  line-height: 1.5;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .content-container {
    grid-template-columns: 1fr;
  }

  .replay-header {
    position: static;
    margin: -15px -15px 15px -15px;
  }

  .filter-group {
    flex-direction: column;
  }

  .soft-filter {
    width: 100%;
  }

  .comment-btn {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    float: none;
  }
}
.content-container,
.comment-section {
  opacity: 0;
  animation: fadeSlideUp 0.6s ease-out forwards;
  animation-delay: 0.3s;
}

.replay-item {
  opacity: 0;
  transform: translateY(20px);
  animation: itemEnter 0.5s ease-out forwards;
}

@keyframes fadeSlideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes itemEnter {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 为列表项添加延迟动画 */
.replay-item:nth-child(1) { animation-delay: 0.4s; }
.replay-item:nth-child(2) { animation-delay: 0.5s; }
.replay-item:nth-child(3) { animation-delay: 0.6s; }
.replay-item:nth-child(n+4) { animation-delay: 0.7s; }

/* 视频容器单独动画 */
.video-container {
  opacity: 0;
  transform: translateY(30px);
  animation: fadeSlideUp 0.6s ease-out forwards;
  animation-delay: 0.2s;
}

/* 评论区输入框动画 */
.comment-input {
  opacity: 0;
  animation: fadeIn 0.4s ease-out forwards;
  animation-delay: 0.8s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>

