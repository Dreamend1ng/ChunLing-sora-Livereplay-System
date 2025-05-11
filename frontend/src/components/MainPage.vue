<template>
  <nav>
    <div class="logo"></div>
    <h3 class="title">椿忆·时空匣 - 椿翎Sora的录像观看站</h3>
    <div class="nav-links">
      <router-link id="a" to="home">首页</router-link>
      <router-link id="a" to="record">回放直播</router-link>
      <router-link id="a" to="login">小羽毛注册/登录</router-link>
      <router-view></router-view>
    </div>
  </nav>

  <div class="banner-container">
    <div class="banner-image"></div>
    <div class="banner-text">
      ೀ⊹⁺ 𝒱𝒶𝓁𝓁ℯ𝓎 𝒮𝓅𝒶𝓇𝓇ℴ𝓌 ഒ₊⁺<br>
      生命如暗室 不妨我明写春诗
    </div>
  </div>
</template>

<script setup lang="js">


</script>


<style scoped>
* {
  max-width: none;
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

.nav-links #a {
  color: #ffffff;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.3s;
}

.nav-links #a:hover {
  color: #ffec99;
}

.banner-container {
  position: relative;
  width: 100%;
  height: 100vh;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  /* 请替换实际图片路径 */
  background: url('../assets/main-bg.png') center/cover;
}

.banner-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) !important; /* 添加!important确保覆盖动画效果 */
  text-align: center;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  color: #ffffff;
  font-size: 45px;
  width: 100%;
  margin: 0;
  padding: 0;
}

.banner-text {
  opacity: 0;
  animation: 
      fadeSlideUp 0.8s ease-out forwards, 
      textGlow 2s ease-in-out infinite;
  animation-delay: 0.6s;
  transform: translate(-50%, -50%) !important; /* 确保动画后保持居中 */
}
.banner-container {
  opacity: 0;
  animation: fadeSlideUp 0.6s ease-out forwards;
  animation-delay: 0.3s;
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

@keyframes textGlow {
  0%, 100% { text-shadow: 0 0 10px rgba(255,255,255,0.3); }
  50% { text-shadow: 0 0 20px rgba(255,255,255,0.6), 0 0 30px rgba(255,255,255,0.4); }
}

/* 导航栏保持无动画 */
nav {
  animation: none !important;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .banner-text {
    font-size: 1.8rem;
    padding: 0 15px;
    animation-delay: 0.4s;
  }

  .banner-container {
    animation-delay: 0.2s;
  }
}
.banner-image {
  opacity: 0;
  animation: scaleIn 1s ease-out forwards;
  animation-delay: 0.2s;
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(1.1);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
