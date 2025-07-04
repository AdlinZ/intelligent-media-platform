<template>
  <div class="features-container">
    <div class="features-header">
      <h1>项目功能介绍</h1>
      <p>探索我们平台的核心功能和特性</p>
    </div>

    <div class="features-grid">
      <!-- 用户认证功能 -->
      <div class="feature-card">
        <div class="feature-icon">
          <User /> <!-- Element Plus图标 -->
        </div>
        <h2>用户认证</h2>
        <ul>
          <li>安全的注册与登录系统</li>
          <li>JWT令牌身份验证</li>
          <li>基于角色的权限管理</li>
        </ul>
      </div>

      <!-- 内容管理功能 -->
      <div class="feature-card">
        <div class="feature-icon">
          <Document /> <!-- Element Plus图标 -->
        </div>
        <h2>内容管理</h2>
        <ul>
          <li>创建、编辑和删除内容</li>
          <li>内容历史版本追踪</li>
          <li>富文本编辑器支持</li>
        </ul>
      </div>

      <!-- 主题管理功能 -->
      <div class="feature-card">
        <div class="feature-icon">
          <Layout /> <!-- Element Plus图标 -->
        </div>
        <h2>主题管理</h2>
        <ul>
          <li>多主题支持</li>
          <li>自定义主题设置</li>
          <li>主题预览功能</li>
        </ul>
      </div>

      <!-- 数据分析功能 -->
      <div class="feature-card">
        <div class="feature-icon">
          <BarChart /> <!-- Element Plus图标 -->
        </div>
        <h2>数据分析</h2>
        <ul>
          <li>内容统计与分析</li>
          <li>用户行为追踪</li>
          <li>可视化数据展示</li>
        </ul>
      </div>
    </div>

    <div class="tech-stack">
      <h2>技术栈</h2>
      <div class="tech-logos">
        <span>Spring Boot</span>
        <span>Vue.js</span>
        <span>Element Plus</span>
        <span>MySQL</span>
        <span>JWT</span>
      </div>
    </div>
  </div>
</template>

<script>
import { User, Document, Layout, BarChart } from '@element-plus/icons-vue';

export default {
  name: 'Features',
  components: {
    User,
    Document,
    Layout,
    BarChart
  }
};
</script>

<style scoped>
/* 基础样式 */
.features-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.features-header {
  text-align: center;
  margin-bottom: 3rem;
}

.features-header h1 {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

/* 功能卡片网格 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-bottom: 4rem;
}

.feature-card {
  background: #fff;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 2.5rem;
  color: #126bae;
  margin-bottom: 1rem;
}

.feature-card h2 {
  margin-bottom: 1rem;
  color: #333;
}

.feature-card ul {
  padding-left: 1.5rem;
}

.feature-card li {
  margin-bottom: 0.5rem;
  color: #666;
}

/* 技术栈部分 */
.tech-stack {
  text-align: center;
  padding: 2rem;
  background: #f5f7fa;
  border-radius: 8px;
}

.tech-logos {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1.5rem;
  margin-top: 1rem;
}

.tech-logos span {
  background: #eef1f6;
  padding: 0.5rem 1.5rem;
  border-radius: 20px;
  font-weight: 500;
}
</style>