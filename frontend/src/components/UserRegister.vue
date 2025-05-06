/* eslint-disable */
<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const formData = ref({
  username: '',
  password: '',
  passwordConfirm: '',
  mail: ''
})

const handleSubmit = async (e) => {
  e.preventDefault()

  // 验证两次密码是否一致
  if (formData.value.password !== formData.value.passwordConfirm) {
    alert('两次输入的密码不一致')
    return
  }

  try {
    const response = await axios.post('http://localhost:9150/api/user/register', formData.value)

    if (response.data === 1) {
      alert('注册成功！点击确定跳转到登录页面')
      router.push('/login')
    } else {
      alert(`出现错误！请将错误截图联系管理员梦末\n${response.data}`)
    }
  } catch (error) {
    alert(`出现错误！请将错误截图联系管理员梦末\n${error.response?.data || error.message}`)
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
      <router-view></router-view>
    </div>
  </nav>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <h1>小羽毛登录</h1>
        <p>请输入您的账号信息</p>
      </div>
      <form @submit="handleSubmit">
        <div class="input-group">
          <input type="text" id="username" v-model="formData.username" required>
          <label for="username">用户名 / 邮箱</label>
        </div>
        <div class="input-group">
          <input type="password" id="password" v-model="formData.password" required>
          <label for="password">密码</label>
        </div>
        <div class="input-group">
          <input type="password" id="password-correct" v-model="formData.passwordConfirm" required>
          <label for="password-correct">密码确认</label>
        </div>
        <div class="input-group">
          <input type="text" id="mail" v-model="formData.mail" required>
          <label for="mail">邮箱</label>
        </div>
        <button type="submit">立即注册</button>
        <div class="additional-links">
          <router-link to="register">有账号的小羽毛来这里登录</router-link>
        </div>
      </form>
    </div>
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
  position: relative;
  z-index: 1000;
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

.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-right: 15%;
  padding-top: 80px;
}

.login-container {
  background: rgba(255, 255, 255, 0.95);
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  width: 400px;
  transform: translateY(-40px);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  color: #db6677;
  font-size: 2em;
  margin-bottom: 10px;
}

.login-header p {
  color: #666;
}

.input-group {
  margin-bottom: 25px;
  position: relative;
}

.input-group input {
  width: 100%;
  padding: 15px;
  border: 2px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.input-group input:focus {
  outline: none;
  border-color: #db6677;
}

.input-group label {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  pointer-events: none;
  transition: 0.3s ease;
}

.input-group input:focus ~ label,
.input-group input:valid ~ label {
  top: -10px;
  left: 5px;
  font-size: 12px;
  background: white;
  padding: 0 5px;
  color: #db6677;
}

button {
  width: 100%;
  padding: 15px;
  background: #db6677;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s ease;
}

button:hover {
  background: #bf5465;
}

.additional-links {
  text-align: center;
  margin-top: 20px;
}

.additional-links a {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.additional-links a:hover {
  color: #db6677;
}

@media (max-width: 768px) {
  .login-page {
    padding-right: 5%;
    justify-content: center;
  }

  .login-container {
    width: 90%;
    padding: 30px;
    transform: translateY(0);
  }

  nav {
    padding: 1rem;
  }

  .nav-links {
    gap: 1rem;
  }

  .title {
    font-size: 18px;
  }
}
.login-page {
  opacity: 0;
  animation: fadeSlideUp 0.6s ease-out forwards;
  animation-delay: 0.3s;
}

.login-container {
  opacity: 0;
  transform: translateY(20px);
  animation: itemEnter 0.6s ease-out forwards;
  animation-delay: 0.4s;
}

.login-header h1 {
  opacity: 0;
  animation: fadeIn 0.5s ease-out forwards;
  animation-delay: 0.5s;
}

.login-header p {
  opacity: 0;
  animation: fadeIn 0.5s ease-out forwards;
  animation-delay: 0.6s;
}

.input-group {
  opacity: 0;
  transform: translateY(10px);
  animation: itemEnter 0.4s ease-out forwards;
}

.input-group:nth-child(1) { animation-delay: 0.6s; }
.input-group:nth-child(2) { animation-delay: 0.7s; }

button {
  opacity: 0;
  animation: fadeIn 0.4s ease-out forwards;
  animation-delay: 0.8s;
}

.additional-links {
  opacity: 0;
  animation: fadeIn 0.4s ease-out forwards;
  animation-delay: 0.9s;
}

/* 复用原有动画 */
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

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* 保持导航栏无动画 */
nav {
  animation: none !important;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .login-container {
    animation-delay: 0.3s;
  }

  .login-header h1 {
    animation-delay: 0.4s;
  }

  .input-group:nth-child(1) { animation-delay: 0.5s; }
  .input-group:nth-child(2) { animation-delay: 0.6s; }

  button {
    animation-delay: 0.7s;
  }

  .additional-links {
    animation-delay: 0.8s;
  }
}
</style>