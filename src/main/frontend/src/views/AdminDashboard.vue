<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon users">👥</div>
        <div class="stat-content">
          <h3>{{ dashboardData?.statistics?.totalUsers || 0 }}</h3>
          <p>总用户数</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon contents">📝</div>
        <div class="stat-content">
          <h3>{{ dashboardData?.statistics?.totalContents || 0 }}</h3>
          <p>总内容数</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon pending">⏳</div>
        <div class="stat-content">
          <h3>{{ dashboardData?.statistics?.pendingContents || 0 }}</h3>
          <p>待审核内容</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon published">✅</div>
        <div class="stat-content">
          <h3>{{ dashboardData?.statistics?.publishedContents || 0 }}</h3>
          <p>已发布内容</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon today">📈</div>
        <div class="stat-content">
          <h3>{{ dashboardData?.statistics?.todayRegistrations || 0 }}</h3>
          <p>今日注册</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon views">👁️</div>
        <div class="stat-content">
          <h3>{{ dashboardData?.statistics?.todayViews || 0 }}</h3>
          <p>今日浏览量</p>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-container">
        <h3>内容状态分布</h3>
        <div class="chart-content">
          <div class="pie-chart">
            <div class="chart-item">
              <div class="chart-color published"></div>
              <span>已发布: {{ dashboardData?.contentStatusDistribution?.已发布 || 0 }}</span>
            </div>
            <div class="chart-item">
              <div class="chart-color pending"></div>
              <span>待审核: {{ dashboardData?.contentStatusDistribution?.待审核 || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="chart-container">
        <h3>用户注册趋势</h3>
        <div class="chart-content">
          <div class="bar-chart">
            <div class="bar-item">
              <div class="bar-label">今日</div>
              <div class="bar" :style="{ height: getBarHeight(dashboardData?.userRegistrationTrend?.今日 || 0) }"></div>
              <div class="bar-value">{{ dashboardData?.userRegistrationTrend?.今日 || 0 }}</div>
            </div>
            <div class="bar-item">
              <div class="bar-label">本周</div>
              <div class="bar" :style="{ height: getBarHeight(dashboardData?.userRegistrationTrend?.本周 || 0) }"></div>
              <div class="bar-value">{{ dashboardData?.userRegistrationTrend?.本周 || 0 }}</div>
            </div>
            <div class="bar-item">
              <div class="bar-label">本月</div>
              <div class="bar" :style="{ height: getBarHeight(dashboardData?.userRegistrationTrend?.本月 || 0) }"></div>
              <div class="bar-value">{{ dashboardData?.userRegistrationTrend?.本月 || 0 }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 最近内容 -->
    <div class="recent-section">
      <div class="section-header">
        <h3>最近内容</h3>
        <router-link to="/admin/content" class="view-all">查看全部</router-link>
      </div>
      
      <div class="content-list">
        <div 
          v-for="content in dashboardData?.recentContents" 
          :key="content.id"
          class="content-item"
        >
          <div class="content-info">
            <h4>{{ content.title }}</h4>
            <p>作者: {{ content.author }} | 创建时间: {{ content.createdAt }}</p>
          </div>
          <div class="content-stats">
            <span class="status" :class="content.status === '已发布' ? 'published' : 'pending'">
              {{ content.status }}
            </span>
            <span class="views">👁️ {{ content.viewCount }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 最近用户 -->
    <div class="recent-section">
      <div class="section-header">
        <h3>最近用户</h3>
        <router-link to="/admin/users" class="view-all">查看全部</router-link>
      </div>
      
      <div class="user-list">
        <div 
          v-for="user in dashboardData?.recentUsers" 
          :key="user.id"
          class="user-item"
        >
          <div class="user-info">
            <h4>{{ user.username }}</h4>
            <p>{{ user.email }}</p>
            <p>注册时间: {{ user.registeredAt }}</p>
          </div>
          <div class="user-stats">
            <span class="content-count">📝 {{ user.contentCount }} 篇内容</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

export default {
  name: 'AdminDashboard',
  setup() {
    const dashboardData = ref(null)
    const loading = ref(false)
    
    const fetchDashboardData = async () => {
      loading.value = true
      try {
        const response = await request.get('/admin/dashboard')
        dashboardData.value = response.data
      } catch (error) {
        console.error('获取仪表板数据失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const getBarHeight = (value) => {
      const maxValue = Math.max(
        dashboardData.value?.userRegistrationTrend?.今日 || 0,
        dashboardData.value?.userRegistrationTrend?.本周 || 0,
        dashboardData.value?.userRegistrationTrend?.本月 || 0
      )
      return maxValue > 0 ? `${(value / maxValue) * 100}%` : '0%'
    }
    
    onMounted(() => {
      fetchDashboardData()
    })
    
    return {
      dashboardData,
      loading,
      getBarHeight
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.users { background: #e3f2fd; }
.stat-icon.contents { background: #f3e5f5; }
.stat-icon.pending { background: #fff3e0; }
.stat-icon.published { background: #e8f5e8; }
.stat-icon.today { background: #fce4ec; }
.stat-icon.views { background: #e0f2f1; }

.stat-content h3 {
  margin: 0 0 4px 0;
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.stat-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.chart-container {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.chart-container h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.pie-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chart-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chart-color {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}

.chart-color.published { background: #4caf50; }
.chart-color.pending { background: #ff9800; }

.bar-chart {
  display: flex;
  align-items: end;
  gap: 20px;
  height: 120px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.bar-label {
  font-size: 12px;
  color: #666;
}

.bar {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px 4px 0 0;
  min-height: 4px;
  transition: height 0.3s ease;
}

.bar-value {
  font-size: 12px;
  font-weight: 600;
  color: #333;
}

.recent-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.view-all {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
}

.view-all:hover {
  text-decoration: underline;
}

.content-list, .user-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.content-item, .user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  transition: background 0.2s ease;
}

.content-item:hover, .user-item:hover {
  background: #f8f9fa;
}

.content-info h4, .user-info h4 {
  margin: 0 0 4px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.content-info p, .user-info p {
  margin: 0 0 2px 0;
  color: #666;
  font-size: 14px;
}

.content-stats, .user-stats {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status.published {
  background: #e8f5e8;
  color: #2e7d32;
}

.status.pending {
  background: #fff3e0;
  color: #f57c00;
}

.views, .content-count {
  color: #666;
  font-size: 14px;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }
  
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .content-item, .user-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style> 