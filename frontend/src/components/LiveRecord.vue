<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import 'video.js/dist/video-js.css'
import videojs from 'video.js'
import axios from 'axios'

const replays = ref([])
const loading = ref(true)
const error = ref(null)
const currentVideoId = ref(null) // 新增当前视频ID
const comments = ref([]) // 清空测试数据
const newComment = ref('')
const currentUser = ref(window.currentUser || '匿名用户')



// 从API获取回放数据
const fetchReplays = async (date = '') => {
  try {
    let apiUrl = 'http://localhost:9150/api/replay/getReplay';
    if (date) {
      apiUrl = `http://localhost:9150/api/replay/getReplayByDate/${date}`;
    }

    console.log('Api to request:', apiUrl)
    const response = await axios.get(apiUrl);
    if (response.status !== 200) throw new Error('获取回放数据失败')
    const data = response.data
    console.log('API返回的数据:', data)
    console.log('API返回的数据长度:', data.length)

    if (data.length > 0) {
      console.log('第一个数据项:', data[0])
      console.log('第一个数据项类型:', typeof data[0])
    }

    if (data.every(item => typeof item === 'string')) {
      replays.value = data.map(item => {
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

// 获取评论方法
const fetchComments = async (videoId) => {
  try {
    const response = await axios.get(`http://localhost:9150/api/comment/getComments/${videoId}`)
    comments.value = response.data.map(commentStr => {
      const comment = {}
      commentStr.split(', ').forEach(pair => {
        const [key, value] = pair.split(': ')  // 移除小写转换
        comment[key] = value
      })
      return {
        id: comment['commentID'], // 保持原始字段名称
        user: comment['username'],
        content: comment['comment'],
        time: new Date(comment['comment_time']).toLocaleString()
      }
    })
  } catch (err) {
    console.error('获取评论失败:', err)
    error.value = '获取评论失败'
  }
}

// 新增删除评论方法
const deleteComment = async (commentId) => {
  try {
    if (!commentId) {
      throw new Error('无效的评论ID')
    }
    console.log('尝试删除评论ID:', commentId)
    await axios.post('http://localhost:9150/api/comment/deleteComment', {
      comment_id: commentId,
    })
    
    await fetchComments(currentVideoId.value)
  } catch (err) {
    console.error('删除评论失败:', err)
    error.value = '删除失败：' + (err.response?.data?.message || err.message)
  }
}

onMounted(() => {
  fetchReplays()
  player = videojs('my-video', {
    controls: true,
    width:800,
    height:580,
  })
})

// 销毁播放器
onBeforeUnmount(() => {
  if (player) {
    player.dispose()
  }
})

// 筛选后的回放列表
const filterDate = ref('')
let player = null

// 监听日期筛选条件变化，触发数据重新加载
watch(filterDate, (newVal) => {
  if (newVal) {
    fetchReplays(newVal)
  } else {
    fetchReplays()
  }
})

/*const filteredReplays = computed(() => {
  return replays.value.filter(item => {
    console.log('Filter date:', filterDate.value)
    const dateMatch = !filterDate.value || item.date.includes(filterDate.value)
    console.log('Filtering item:', item)
    console.log('DateMatch:', dateMatch)
    return dateMatch
  })
})*/


// 修改后的播放视频函数
const playVideo = async (id, title) => {
  if (!player || !id) return;

  try {
    loading.value = true;
    error.value = null;
    currentVideoId.value = id
    const videoUrl = `http://localhost:9150/api/replay/playReplay/${id}`;
    player.src({ type: 'video/mp4', src: videoUrl });
    player.play();
    document.getElementById('videoInfo').textContent = title;
    await fetchComments(id) // 播放时获取评论
  } catch (err) {
    console.error('播放视频时出错:', err);
    error.value = '播放视频失败: ' + err.message;
  } finally {
    loading.value = false;
  }
}

// 修改后的提交评论方法
const submitComment = async () => {
  if (!newComment.value.trim() || !currentVideoId.value) return
  
  try {
    const currentUser = window.currentUser || '匿名用户'
    await axios.post('http://localhost:9150/api/comment/sendComment', {
      video_id: currentVideoId.value,
      user_id: currentUser,
      content: newComment.value.trim()
    })
    
    await fetchComments(currentVideoId.value)
    newComment.value = ''
  } catch (err) {
    console.error('提交评论失败:', err)
    error.value = '评论发送失败，请稍后重试'
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
          <div v-if="loading" class="loading">加载中...</div>
          <div v-else-if="error" class="error">{{ error }}</div>
          <template v-else>
            <div class="replay-header">
              <div class="filter-group">
                
                <input type="date" v-model="filterDate" @change="fetchReplays(filterDate)" class="soft-filter">
              </div>
            </div>

            <div
                v-for="item in replays" 
                :key="item.id"
                class="replay-item"
                @click="playVideo(item.id, item.title)"
            >
              <div>{{ item.title }}</div>
              <div class="meta-info">
                {{ item.type }} ·
                {{ item.date }}
              </div>
            </div>
          </template>
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
          <div class="comment-content">
            {{ comment.content }}
            <button 
              v-if="currentUser === comment.user"
              @click.stop="deleteComment(comment.id)"
              class="delete-btn"
            >
              删除
            </button>
          </div>
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
  width: 800px;      /* 固定宽度 */
  height: 615px;     /* 固定高度 */
  margin: 0 auto;    /* 居中显示 */
}

.video-info {
  margin-top: 5px;
  padding: 5px;
  background-color: #fff;
  border-radius: 4px;
  color: #333;
}

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
  align-items: center;
  margin-bottom: 8px;
  font-size: 0.9em;
}

.comment-user {
  color: #db6677;
  font-weight: 500;
  margin-right: 8px;
}

.comment-time {
  color: #999;
  font-size: 0.8em;
}

.comment-content {
  color: #666;
  line-height: 1.5;
  text-indent: 2em;
  text-align: left;
  position: relative;
  min-height: 40px;  /* 保证单行评论也有足够空间 */
}

.delete-btn {
  position: absolute;
  right: 0;
  bottom: 0;
  color: #db6677;
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.8em;
  transform: translateY(25%); /* 微调垂直位置 */
}

.delete-btn:hover {
  background: rgba(219, 102, 119, 0.1);
}

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

.replay-item:nth-child(1) { animation-delay: 0.4s; }
.replay-item:nth-child(2) { animation-delay: 0.5s; }
.replay-item:nth-child(3) { animation-delay: 0.6s; }
.replay-item:nth-child(n+4) { animation-delay: 0.7s; }

.video-container {
  opacity: 0;
  transform: translateY(30px);
  animation: fadeSlideUp 0.6s ease-out forwards;
  animation-delay: 0.2s;
}

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