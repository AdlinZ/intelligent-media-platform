<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <h3 v-if="!sidebarCollapsed">管理后台</h3>
        <button class="collapse-btn" @click="toggleSidebar">
          <span v-if="sidebarCollapsed">☰</span>
          <span v-else>×</span>
        </button>
      </div>
      
      <nav class="sidebar-nav">
        <router-link 
          to="/admin/dashboard" 
          class="nav-item"
          :class="{ active: $route.path === '/admin/dashboard' }"
        >
          <span class="nav-icon">📊</span>
          <span v-if="!sidebarCollapsed" class="nav-text">仪表板</span>
        </router-link>
        
        <router-link 
          to="/admin/content" 
          class="nav-item"
          :class="{ active: $route.path.startsWith('/admin/content') }"
        >
          <span class="nav-icon">📝</span>
          <span v-if="!sidebarCollapsed" class="nav-text">内容管理</span>
        </router-link>
        
        <router-link 
          to="/admin/users" 
          class="nav-item"
          :class="{ active: $route.path.startsWith('/admin/users') }"
        >
          <span class="nav-icon">👥</span>
          <span v-if="!sidebarCollapsed" class="nav-text">用户管理</span>
        </router-link>
        
        <router-link 
          to="/admin/admins" 
          class="nav-item"
          :class="{ active: $route.path.startsWith('/admin/admins') }"
        >
          <span class="nav-icon">🔧</span>
          <span v-if="!sidebarCollapsed" class="nav-text">管理员</span>
        </router-link>
      </nav>
    </div>
    
    <!-- 主内容区 -->
    <div class="main-content" :class="{ expanded: sidebarCollapsed }">
      <!-- 顶部导航栏 -->
      <header class="top-header">
        <div class="header-left">
          <h2>{{ pageTitle }}</h2>
        </div>
        
        <div class="header-right">
          <div class="admin-info">
            <span class="admin-name">{{ adminInfo?.realName || adminInfo?.username }}</span>
            <button class="logout-btn" @click="handleLogout">退出</button>
          </div>
        </div>
      </header>
      
      <!-- 页面内容 -->
      <main class="page-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'AdminLayout',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const sidebarCollapsed = ref(false)
    const adminInfo = ref(null)
    
    // 页面标题映射
    const pageTitles = {
      '/admin/dashboard': '仪表板',
      '/admin/content': '内容管理',
      '/admin/users': '用户管理',
      '/admin/admins': '管理员管理'
    }
    
    const pageTitle = computed(() => {
      return pageTitles[route.path] || '管理后台'
    })
    
    const toggleSidebar = () => {
      sidebarCollapsed.value = !sidebarCollapsed.value
    }
    
    const handleLogout = () => {
      localStorage.removeItem('adminInfo')
      localStorage.removeItem('isAdmin')
      router.push('/admin/login')
    }
    
    onMounted(() => {
      const storedAdminInfo = localStorage.getItem('adminInfo')
      if (storedAdminInfo) {
        adminInfo.value = JSON.parse(storedAdminInfo)
      }
    })
    
    return {
      sidebarCollapsed,
      adminInfo,
      pageTitle,
      toggleSidebar,
      handleLogout
    }
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 250px;
  background: #2c3e50;
  color: white;
  transition: width 0.3s ease;
  position: fixed;
  height: 100vh;
  z-index: 1000;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #34495e;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.collapse-btn {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  padding: 5px;
}

.sidebar-nav {
  padding: 20px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  color: #bdc3c7;
  text-decoration: none;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background: #34495e;
  color: white;
}

.nav-item.active {
  background: #3498db;
  color: white;
  border-left-color: #2980b9;
}

.nav-icon {
  font-size: 18px;
  margin-right: 12px;
  width: 20px;
  text-align: center;
}

.nav-text {
  font-size: 14px;
  font-weight: 500;
}

.main-content {
  flex: 1;
  margin-left: 250px;
  transition: margin-left 0.3s ease;
  display: flex;
  flex-direction: column;
}

.main-content.expanded {
  margin-left: 60px;
}

.top-header {
  background: white;
  padding: 20px 30px;
  border-bottom: 1px solid #e1e5e9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.admin-name {
  color: #333;
  font-weight: 500;
}

.logout-btn {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s ease;
}

.logout-btn:hover {
  background: #c0392b;
}

.page-content {
  flex: 1;
  padding: 30px;
  background: #f8f9fa;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  
  .sidebar.collapsed {
    transform: translateX(0);
    width: 250px;
  }
  
  .main-content {
    margin-left: 0;
  }
  
  .main-content.expanded {
    margin-left: 0;
  }
}
</style> 