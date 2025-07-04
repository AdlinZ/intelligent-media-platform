<template>
  <div id="app">
    <el-container class="app-container">
      <!-- 固定顶栏 -->
      <el-header class="app-header">
        <div class="logo-container">
          <el-icon class="logo-icon"><PenTool /></el-icon>
          <span class="logo-text">MediaGenius</span>
          <span class="platform-text">智能自媒体内容创作平台</span>
        </div>
        <el-menu :default-active="activeIndex" mode="horizontal" class="header-menu">
          <el-menu-item index="1" @click="goToHome">首页</el-menu-item>
          <el-menu-item index="2" @click="goToFeatures">功能介绍</el-menu-item>
          <el-menu-item index="3" @click="goToTutorial">使用教程</el-menu-item>
          <el-menu-item index="4" @click="goToArticles">文章列表</el-menu-item>
          <el-menu-item index="5" @click="goToTopicSelection">选题助手</el-menu-item>
          <el-menu-item index="6" @click="goToDashboard">数据看板</el-menu-item>
          <el-menu-item v-if="isLoggedIn" index="7" @click="goToProfile">个人中心</el-menu-item>
        </el-menu>
        <div class="auth-buttons">
          <!-- 动态显示登录状态 -->
          <template v-if="isLoggedIn">
            <el-dropdown class="user-dropdown">
              <span class="user-name">{{ username }}</span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goToProfile">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="text" class="login-btn" @click="goToLogin">登录</el-button>
            <el-button type="primary" class="register-btn" @click="goToRegister">免费注册</el-button>
          </template>
        </div>
      </el-header>

      <!-- 主内容区域 -->
      <el-main class="app-main">
        <router-view />
      </el-main>

      <!-- 底栏 -->
      <el-footer class="app-footer">
        <div class="footer-container">
          <div class="footer-logo">MediaGenius</div>
          
          <div class="footer-links">
            <!-- 产品链接组 -->
            <div class="link-group">
              <h3 class="link-title">产品</h3>
              <a class="footer-link">功能介绍</a>
              <a class="footer-link">定价方案</a>
              <a class="footer-link">更新日志</a>
            </div>
          
            <!-- 资源链接组 -->
            <div class="link-group">
              <h3 class="link-title">资源</h3>
              <a class="footer-link">帮助中心</a>
              <a class="footer-link">教程文档</a>
              <a class="footer-link">常见问题</a>
            </div>
          
            <!-- 公司链接组 -->
            <div class="link-group">
              <h3 class="link-title">公司</h3>
              <a class="footer-link">关于我们</a>
              <a class="footer-link">联系我们</a>
              <a class="footer-link">加入我们</a>
            </div>
          </div>
          
          <div class="copyright">
            智能自媒体内容创作与运营平台 ©2025 MediaGenius. 保留所有权利。
          </div>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { PenTool } from '@element-plus/icons-vue';
import request from './utils/request';
import { ElMessage } from 'element-plus';

// 路由相关
const router = useRouter();
const route = useRoute();

// 响应式变量
const activeIndex = ref('1');
const isLoggedIn = ref(false);
const username = ref('');

// 导航方法
const goToHome = () => router.push('/');
const goToFeatures = () => router.push('/features');
const goToTutorial = () => router.push('/tutorial');
const goToAbout = () => router.push('/about');
const goToTopicSelection = () => router.push('/topics');
const goToDashboard = () => router.push('/dashboard');
const goToLogin = () => router.push('/login');
const goToRegister = () => router.push('/register');
const goToProfile = () => router.push('/profile');
const goToArticles = () => router.push('/articles');

// 更新激活菜单
const updateActiveMenu = (path) => {
  switch(path) {
    case '/': activeIndex.value = '1'; break;
    case '/features': activeIndex.value = '2'; break;
    case '/tutorial': activeIndex.value = '3'; break;
    case '/about': activeIndex.value = '4'; break;
    case '/topics': activeIndex.value = '5'; break;
    case '/dashboard': activeIndex.value = '6'; break;
    case '/profile': activeIndex.value = '7'; break;
    default: activeIndex.value = '1';
  }
};

// 获取用户信息
const getUserInfo = async () => {
  try {
    // 修复URL，移除重复的/api前缀
    const response = await request.get('/auth/me');
    username.value = response.username || '用户';
    isLoggedIn.value = true;
  } catch (error) {
    // token无效或过期
    localStorage.removeItem('token');
    localStorage.removeItem('tokenType');
    isLoggedIn.value = false;
    username.value = '';
  }
};

// 退出登录
const logout = async () => {
  try {
    await request.post('/api/auth/logout');
  } catch (error) {
    console.error('Logout error:', error);
  } finally {
    localStorage.removeItem('token');
    localStorage.removeItem('tokenType');
    isLoggedIn.value = false;
    username.value = '';
    router.push('/login');
    ElMessage.success('已退出登录');
  }
};

// 组件挂载时初始化
onMounted(() => {
  updateActiveMenu(route.path);
  router.beforeEach((to) => updateActiveMenu(to.path));
  
  // 检查登录状态
  const token = localStorage.getItem('token');
  if (token) {
    getUserInfo();
  }
});

// 监听路由变化，更新登录状态
watch(
  () => route.path,
  () => {
    const token = localStorage.getItem('token');
    if (token && !isLoggedIn.value) {
      getUserInfo();
    } else if (!token && isLoggedIn.value) {
      isLoggedIn.value = false;
    }
  }
);
</script>

<style scoped>
/* 全局样式 */
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 容器布局 */
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 固定顶栏 */
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background-color: #126bae;
  color: white;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

/* Logo样式 */
.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  color: #FFD04B;
  font-size: 24px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
}

.platform-text {
  color: rgba(255,255,255,0.8);
  font-size: 14px;
  margin-left: 10px;
  padding-left: 10px;
  border-left: 1px solid rgba(255,255,255,0.3);
}

/* 导航菜单 */
.header-menu {
  background-color: transparent;
  color: white;
  margin-left: 20px;
}

.el-menu-item {
  color: white;
}

.el-menu-item.is-active {
  color: #FFD04B;
}

/* 登录注册按钮 */
.auth-buttons {
  margin-left: auto;
  display: flex;
  gap: 10px;
}

/* 主内容区域 */
.app-main {
  margin-top: 60px; /* 与顶栏高度一致 */
  flex: 1;
  padding: 20px;
  background-color: #f5f5f5;
}

/* 底栏样式 */
.app-footer {
  background-color: #126bae !important; /* 与主内容区域背景色统一 */
  color: #ffffff;   
  width: 100%;
  min-height: 200px;
  padding: 0;
  margin: 0;
}

.footer-container {
  width: 100%;
  margin: 0 auto;
  padding: 3rem 2rem;
  text-align: center;
  background-color: inherit;
}

/* 底栏链接 */
.footer-links {
  display: flex;
  flex-wrap: wrap;
  gap: 4rem;
  margin: 2rem 0;
  justify-content: center;
  padding: 0 1rem;
}

.link-title {
  font-size: 1rem;
  margin-bottom: 1rem;
  color: #333; /* 调整标题颜色 */
  font-weight: bold;
}

.footer-link {
  display: block;
  color: #666; /* 调整链接颜色 */
  margin-bottom: 0.8rem;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-link:hover {
  color: #126bae; /* 使用主题蓝色作为hover效果 */
}

/* 版权信息 */
.copyright {
  text-align: center;
  padding-top: 2rem;
  border-top: 1px solid #e0e0e0; /* 调整边框颜色 */
  color: #999; /* 调整版权文字颜色 */
  font-size: 0.9rem;
  margin-top: 2rem;
}

/* 确保页面底部无间隙 */
#app, .app-container {
  margin: 0;
  padding: 0;
}

.footer-logo {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
  text-align: center;
}

/* 底栏链接 */
.footer-links {
  display: flex;
  flex-wrap: wrap;
  gap: 4rem;
  margin: 2rem 0;
  justify-content: center;
  padding: 0 1rem;
}

.link-group {
  min-width: 120px;
  text-align: center;
}

.link-title {
  font-size: 1rem;
  margin-bottom: 1rem;
  color: rgba(255, 255, 255, 0.9);
  font-weight: bold;
}

.footer-link {
  display: block;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 0.8rem;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-link:hover {
  color: #FFD04B;
}

/* 版权信息 */
.copyright {
  text-align: center;
  padding-top: 2rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.4);
  font-size: 0.9rem;
  margin-top: 2rem;
}

/* 用户下拉菜单样式 */
.user-dropdown {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.user-name {
  margin-right: 5px;
}
</style>
