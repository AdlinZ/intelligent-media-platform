<template>
  <div class="dashboard-container">
    <!-- 顶部导航栏 -->
    <div class="dashboard-header">
      <div class="header-left">
        <el-button @click="goBack" icon="ArrowLeft" class="back-button" type="primary" plain>
          返回编辑
        </el-button>
        <h1 class="dashboard-title">智能媒体平台 - 数据看板</h1>
      </div>
      <div class="header-right">
        <el-select v-model="timeRange" @change="handleTimeRangeChange" placeholder="选择时间范围" size="small">
          <el-option label="最近7天" value="7"></el-option>
          <el-option label="最近30天" value="30"></el-option>
          <el-option label="最近90天" value="90"></el-option>
        </el-select>
        <el-button @click="refreshData" icon="Refresh" size="small" :loading="loading">
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div v-if="statistics.length === 0" class="no-data">
        <p>正在加载统计数据...</p>
      </div>
      <div class="stat-card" v-for="(stat, index) in statistics" :key="index">
        <div class="stat-icon" :style="{ backgroundColor: stat.color }">
          <i :class="stat.icon"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ formatNumber(stat.value) }}</div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-trend" :class="(stat.trend || 0) > 0 ? 'positive' : 'negative'">
            <i :class="(stat.trend || 0) > 0 ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
            {{ Math.abs(stat.trend || 0) }}%
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <!-- 流量趋势图 -->
      <div class="chart-container">
        <div class="chart-header">
          <h3>流量趋势分析</h3>
          <div class="chart-actions">
            <el-radio-group v-model="trafficMetric" size="small" @change="updateTrafficChart">
              <el-radio-button label="reads">阅读量</el-radio-button>
              <el-radio-button label="likes">点赞数</el-radio-button>
              <el-radio-button label="shares">转发数</el-radio-button>
              <el-radio-button label="comments">评论数</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <div class="chart-content">
          <canvas ref="trafficChart" height="300"></canvas>
        </div>
      </div>

      <!-- 用户参与度饼图 -->
      <div class="chart-container">
        <div class="chart-header">
          <h3>用户参与度分析</h3>
        </div>
        <div class="chart-content">
          <canvas ref="engagementChart" height="300"></canvas>
        </div>
      </div>
    </div>

    <!-- 内容分析区域 -->
    <div class="analysis-section">
      <div class="chart-container">
        <div class="chart-header">
          <h3>内容分类表现</h3>
        </div>
        <div class="chart-content">
          <canvas ref="contentChart" height="300"></canvas>
        </div>
      </div>

      <!-- 实时数据表格 -->
      <div class="table-container">
        <div class="chart-header">
          <h3>热门内容排行</h3>
        </div>
        <el-table :data="hotContent" stripe style="width: 100%">
          <el-table-column prop="rank" label="排名" width="80" align="center">
            <template #default="scope">
              <div class="rank-badge" :class="`rank-${scope.row.rank}`">
                {{ scope.row.rank }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="200"></el-table-column>
          <el-table-column prop="category" label="分类" width="100"></el-table-column>
          <el-table-column prop="reads" label="阅读量" width="100" align="center"></el-table-column>
          <el-table-column prop="likes" label="点赞" width="100" align="center"></el-table-column>
          <el-table-column prop="shares" label="转发" width="100" align="center"></el-table-column>
          <el-table-column prop="engagement" label="参与度" width="100" align="center">
            <template #default="scope">
              <span class="engagement-rate">{{ scope.row.engagement }}%</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <el-loading-spinner></el-loading-spinner>
      <p>正在加载数据...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDashboardStats, getTrafficData, getContentAnalysis, getUserEngagement } from '@/api/topic'
import Chart from 'chart.js/auto'

// 确保Chart.js正确加载
console.log('Chart.js版本:', Chart.version)

const router = useRouter()
const loading = ref(false)
const timeRange = ref('7')
const trafficMetric = ref('reads')

// 图表引用
const trafficChart = ref(null)
const engagementChart = ref(null)
const contentChart = ref(null)

// 数据
const dashboardData = ref({})
const statistics = ref([])
const hotContent = ref([])

// 图表实例
let trafficChartInstance = null
let engagementChartInstance = null
let contentChartInstance = null

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 格式化数字
const formatNumber = (num) => {
  if (num === undefined || num === null || isNaN(num)) {
    return '0'
  }
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toLocaleString()
}

// 处理时间范围变化
const handleTimeRangeChange = async () => {
  await loadTrafficData()
}

// 刷新数据
const refreshData = async () => {
  await loadDashboardData()
}

// 加载Dashboard数据
const loadDashboardData = async () => {
  loading.value = true
  try {
    const response = await getDashboardStats()
    console.log('API响应:', response)
    
    // 检查响应是否为有效的JSON数据
    if (response && typeof response === 'object' && !response.includes('<!doctype html>')) {
      dashboardData.value = response
      
      // 确保数据结构完整
      if (!dashboardData.value.statistics) {
        dashboardData.value.statistics = {}
      }
      if (!dashboardData.value.traffic) {
        dashboardData.value.traffic = {
          dates: [],
          reads: [],
          likes: [],
          shares: [],
          comments: []
        }
      }
      if (!dashboardData.value.userEngagement) {
        dashboardData.value.userEngagement = {
          labels: [],
          data: [],
          colors: []
        }
      }
      
      // 更新统计数据
      updateStatistics()
      
      // 更新图表
      await nextTick()
      updateCharts()
    } else {
      console.warn('API返回了HTML页面，使用默认数据')
      // 保持现有默认数据，不更新
    }
  } catch (error) {
    console.error('Dashboard数据加载失败:', error)
    ElMessage.error('获取数据失败: ' + (error.message || '网络错误'))
    // 保持现有数据，不重新设置默认值
  } finally {
    loading.value = false
  }
}

// 加载流量数据
const loadTrafficData = async () => {
  try {
    const response = await getTrafficData(parseInt(timeRange.value))
    dashboardData.value.traffic = response
    updateTrafficChart()
  } catch (error) {
    ElMessage.error('获取流量数据失败: ' + error.message)
  }
}

// 更新统计数据
const updateStatistics = () => {
  const data = dashboardData.value.statistics || {}
  console.log('更新统计数据:', data)
  
  statistics.value = [
    {
      label: '总阅读量',
      value: data.totalReads || 0,
      trend: data.readsTrend || 0,
      icon: 'el-icon-view',
      color: '#409EFF'
    },
    {
      label: '平均点赞率',
      value: data.avgLikesRate || 0,
      trend: data.likesTrend || 0,
      icon: 'el-icon-star-on',
      color: '#67C23A'
    },
    {
      label: '转发分享数',
      value: data.shares || 0,
      trend: data.sharesTrend || 0,
      icon: 'el-icon-share',
      color: '#E6A23C'
    },
    {
      label: '评论数',
      value: data.comments || 0,
      trend: data.commentsTrend || 0,
      icon: 'el-icon-chat-dot-round',
      color: '#F56C6C'
    },
    {
      label: '内容总数',
      value: data.totalContent || 0,
      trend: data.contentGrowth || 0,
      icon: 'el-icon-document',
      color: '#909399'
    }
  ]
  
  console.log('统计数组:', statistics.value)
}

// 更新流量图表
const updateTrafficChart = () => {
  console.log('更新流量图表...')
  console.log('trafficChart ref:', trafficChart.value)
  console.log('traffic data:', dashboardData.value.traffic)
  
  if (!dashboardData.value.traffic || !trafficChart.value) {
    console.warn('流量图表更新失败：缺少数据或DOM元素')
    return
  }

  const data = dashboardData.value.traffic
  const metric = trafficMetric.value
  
  console.log('流量数据:', data)
  console.log('当前指标:', metric)
  
  // 确保数据存在
  if (!data.dates || !data[metric]) {
    console.warn('流量数据不完整:', data)
    return
  }
  
  if (trafficChartInstance) {
    trafficChartInstance.destroy()
  }

  try {
    const ctx = trafficChart.value.getContext('2d')
    console.log('Canvas context:', ctx)
    
    trafficChartInstance = new Chart(ctx, {
      type: 'line',
      data: {
        labels: data.dates,
        datasets: [{
          label: getMetricLabel(metric),
          data: data[metric],
          borderColor: '#409EFF',
          backgroundColor: 'rgba(64, 158, 255, 0.1)',
          tension: 0.4,
          fill: true
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: {
              color: 'rgba(0, 0, 0, 0.05)'
            }
          },
          x: {
            grid: {
              display: false
            }
          }
        }
      }
    })
    console.log('流量图表创建成功')
  } catch (error) {
    console.error('创建流量图表失败:', error)
  }
}

// 更新参与度图表
const updateEngagementChart = () => {
  console.log('更新参与度图表...')
  console.log('engagementChart ref:', engagementChart.value)
  console.log('userEngagement data:', dashboardData.value.userEngagement)
  
  if (!dashboardData.value.userEngagement || !engagementChart.value) {
    console.warn('参与度图表更新失败：缺少数据或DOM元素')
    return
  }

  const data = dashboardData.value.userEngagement
  
  // 确保数据存在
  if (!data.labels || !data.data || !data.colors) {
    console.warn('用户参与度数据不完整:', data)
    return
  }
  
  if (engagementChartInstance) {
    engagementChartInstance.destroy()
  }

  try {
    const ctx = engagementChart.value.getContext('2d')
    console.log('参与度图表Canvas context:', ctx)
    
    engagementChartInstance = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: data.labels,
        datasets: [{
          data: data.data,
          backgroundColor: data.colors,
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom'
          }
        }
      }
    })
    console.log('参与度图表创建成功')
  } catch (error) {
    console.error('创建参与度图表失败:', error)
  }
}

// 更新内容分析图表
const updateContentChart = async () => {
  console.log('更新内容分析图表...')
  console.log('contentChart ref:', contentChart.value)
  
  if (!contentChart.value) {
    console.warn('内容分析图表更新失败：缺少DOM元素')
    return
  }
  
  try {
    const response = await getContentAnalysis()
    const data = response
    
    console.log('内容分析API响应:', data)
    
    // 检查是否为有效数据
    if (data && typeof data === 'object' && !data.includes && data.categories && data.performance) {
      if (contentChartInstance) {
        contentChartInstance.destroy()
      }

      const ctx = contentChart.value.getContext('2d')
      console.log('内容分析图表Canvas context:', ctx)
      
      contentChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: data.categories,
          datasets: [{
            label: '表现指数',
            data: data.performance,
            backgroundColor: 'rgba(64, 158, 255, 0.8)',
            borderRadius: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              max: 100,
              grid: {
                color: 'rgba(0, 0, 0, 0.05)'
              }
            },
            x: {
              grid: {
                display: false
              }
            }
          }
        }
      })
      console.log('内容分析图表创建成功')
    } else {
      console.warn('内容分析数据不完整或无效，使用默认数据')
      // 使用默认数据
      const defaultData = {
        categories: ['科技', '娱乐', '体育', '财经', '教育'],
        performance: [85, 72, 68, 91, 76]
      }
      
      if (contentChartInstance) {
        contentChartInstance.destroy()
      }
      
      const ctx = contentChart.value.getContext('2d')
      contentChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: defaultData.categories,
          datasets: [{
            label: '表现指数',
            data: defaultData.performance,
            backgroundColor: 'rgba(64, 158, 255, 0.8)',
            borderRadius: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              max: 100,
              grid: {
                color: 'rgba(0, 0, 0, 0.05)'
              }
            },
            x: {
              grid: {
                display: false
              }
            }
          }
        }
      })
      console.log('内容分析图表使用默认数据创建成功')
    }
  } catch (error) {
    console.error('获取内容分析数据失败:', error)
    // 使用默认数据
    if (contentChart.value) {
      const defaultData = {
        categories: ['科技', '娱乐', '体育', '财经', '教育'],
        performance: [85, 72, 68, 91, 76]
      }
      
      if (contentChartInstance) {
        contentChartInstance.destroy()
      }
      
      const ctx = contentChart.value.getContext('2d')
      contentChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: defaultData.categories,
          datasets: [{
            label: '表现指数',
            data: defaultData.performance,
            backgroundColor: 'rgba(64, 158, 255, 0.8)',
            borderRadius: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              max: 100,
              grid: {
                color: 'rgba(0, 0, 0, 0.05)'
              }
            },
            x: {
              grid: {
                display: false
              }
            }
          }
        }
      })
      console.log('内容分析图表使用默认数据创建成功')
    }
  }
}

// 获取指标标签
const getMetricLabel = (metric) => {
  const labels = {
    reads: '阅读量',
    likes: '点赞数',
    shares: '转发数',
    comments: '评论数'
  }
  return labels[metric] || metric
}

// 更新所有图表
const updateCharts = async () => {
  console.log('开始更新图表...')
  console.log('dashboardData:', dashboardData.value)
  
  try {
    await updateTrafficChart()
    await updateEngagementChart()
    await updateContentChart()
    console.log('图表更新完成')
  } catch (error) {
    console.error('图表更新失败:', error)
  }
}

// 生成热门内容数据
const generateHotContent = () => {
  hotContent.value = [
    { rank: 1, title: '人工智能在媒体行业的应用前景', category: '科技', reads: 12580, likes: 845, shares: 326, engagement: 8.5 },
    { rank: 2, title: '2024年内容创作趋势分析', category: '娱乐', reads: 10890, likes: 720, shares: 285, engagement: 7.8 },
    { rank: 3, title: '短视频平台的用户行为研究', category: '科技', reads: 9650, likes: 680, shares: 245, engagement: 7.2 },
    { rank: 4, title: '新媒体营销策略深度解析', category: '财经', reads: 8920, likes: 620, shares: 210, engagement: 6.9 },
    { rank: 5, title: '内容创作者如何提升影响力', category: '教育', reads: 7840, likes: 580, shares: 195, engagement: 6.5 }
  ]
}

onMounted(async () => {
  console.log('Dashboard组件挂载...')
  
  // 立即初始化默认数据，确保页面有内容显示
  dashboardData.value = {
    statistics: {
      totalReads: 12580,
      readsTrend: 12.5,
      avgLikesRate: 3.8,
      likesTrend: 2.3,
      shares: 845,
      sharesTrend: 5.7,
      comments: 326,
      commentsTrend: -1.2,
      totalContent: 156,
      contentGrowth: 8.5
    },
    traffic: {
      dates: ['12/01', '12/02', '12/03', '12/04', '12/05', '12/06', '12/07'],
      reads: [3200, 4500, 4880, 5200, 4800, 5600, 6100],
      likes: [120, 180, 165, 200, 185, 220, 240],
      shares: [230, 310, 305, 350, 320, 380, 420],
      comments: [45, 68, 52, 75, 60, 85, 95]
    },
    userEngagement: {
      labels: ['点赞', '评论', '转发', '收藏'],
      data: [470, 326, 845, 520],
      colors: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C']
    }
  }
  
  console.log('初始化数据完成:', dashboardData.value)
  
  updateStatistics()
  generateHotContent()
  
  // 等待DOM更新后创建图表
  await nextTick()
  console.log('DOM更新完成，开始创建图表...')
  
  // 延迟一点时间确保canvas元素完全渲染
  setTimeout(async () => {
    console.log('开始更新图表...')
    await updateCharts()
  }, 100)
  
  // 然后尝试加载真实数据
  await loadDashboardData()
})
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  background: #ffffff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-button {
  border-radius: 8px;
}

.dashboard-title {
  margin: 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.header-right {
  display: flex;
  gap: 15px;
  align-items: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 8px;
}

.stat-trend {
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-trend.positive {
  color: #27ae60;
}

.stat-trend.negative {
  color: #e74c3c;
}

.charts-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.analysis-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-container {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.chart-content {
  position: relative;
  height: 300px;
}

.table-container {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.rank-badge {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: white;
  font-size: 14px;
}

.rank-1 { background: linear-gradient(45deg, #ffd700, #ffed4e); }
.rank-2 { background: linear-gradient(45deg, #c0c0c0, #e8e8e8); }
.rank-3 { background: linear-gradient(45deg, #cd7f32, #daa520); }
.rank-4, .rank-5 { background: #95a5a6; }

.engagement-rate {
  color: #27ae60;
  font-weight: 600;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-overlay p {
  margin-top: 20px;
  color: #7f8c8d;
  font-size: 16px;
}

.no-data {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.no-data p {
  color: #7f8c8d;
  font-size: 16px;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .analysis-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style>