<template>
  <div class="app-container">
    <!-- 删除原有的<el-header class="navbar">整个导航栏部分 -->

    <!-- 保留英雄区域及以下内容 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">智能自媒体内容创作与运营平台</h1>
        <p class="hero-subtitle">AI驱动的一站式内容解决方案，让创作更高效，运营更简单</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="goToTopicSelection" class="start-button">
            立即开始 <el-icon class="right-icon"><ArrowRight /></el-icon>
          </el-button>
          <el-button type="default" size="large" class="demo-button">
            <el-icon class="play-icon"><PlayCircle /></el-icon> 观看演示
          </el-button>
        </div>
        <div class="stats-container">
          <div class="stat-item">
            <span class="stat-number">10,000+</span>
            <span class="stat-label">活跃创作者</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">98%</span>
            <span class="stat-label">用户满意度</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">50+</span>
            <span class="stat-label">内容模板</span>
          </div>
        </div>
      </div>
      <div class="hero-image">
        <el-image src="https://picsum.photos/id/180/600/400" alt="内容创作平台"></el-image>
      </div>
    </section>

    <!-- 功能特点 -->
    <section class="features-section">
      <div class="section-header">
        <h2 class="section-title">强大功能，简单操作</h2>
        <p class="section-desc">我们提供全方位的自媒体创作工具，满足您的各种需求</p>
      </div>
      <div class="features-grid">
        <el-card class="feature-card">
          <div class="feature-icon"><el-icon><MagicStick /></el-icon></div>
          <h3 class="feature-title">AI智能创作</h3>
          <p class="feature-desc">基于GPT模型的智能内容生成，帮助您快速创作高质量文章</p>
        </el-card>
        <el-card class="feature-card">
          <div class="feature-icon"><el-icon><Layout /></el-icon></div>
          <h3 class="feature-title">多平台发布</h3>
          <p class="feature-desc">一键同步到各大社交平台，节省您的时间和精力</p>
        </el-card>
        <el-card class="feature-card">
          <div class="feature-icon"><el-icon><BarChart /></el-icon></div>
          <h3 class="feature-title">数据分析</h3>
          <p class="feature-desc">详细的阅读量、点赞数分析，助您优化内容策略</p>
        </el-card>
        <el-card class="feature-card">
          <div class="feature-icon"><el-icon><Clock /></el-icon></div>
          <h3 class="feature-title">定时发布</h3>
          <p class="feature-desc">预设发布时间，让您的内容在最佳时段与读者见面</p>
        </el-card>
      </div>
    </section>

    <!-- 使用流程 -->
    <section class="process-section">
      <div class="section-header">
        <h2 class="section-title">简单三步，开启创作之旅</h2>
        <p class="section-desc">无需复杂操作，快速上手自媒体创作</p>
      </div>
      <div class="process-steps">
        <div class="step-item">
          <div class="step-number">1</div>
          <div class="step-icon"><el-icon><Edit /></el-icon></div>
          <h3 class="step-title">选择主题</h3>
          <p class="step-desc">从丰富的主题库中选择您感兴趣的创作方向</p>
        </div>
        <div class="step-connector"><el-icon><ArrowRight /></el-icon></div>
        <div class="step-item">
          <div class="step-number">2</div>
          <div class="step-icon"><el-icon><RefreshLeft /></el-icon></div>
          <h3 class="step-title">AI生成内容</h3>
          <p class="step-desc">输入关键词，AI自动生成高质量文章初稿</p>
        </div>
        <div class="step-connector"><el-icon><ArrowRight /></el-icon></div>
        <div class="step-item">
          <div class="step-number">3</div>
          <div class="step-icon"><el-icon><Send /></el-icon></div>
          <h3 class="step-title">编辑发布</h3>
          <p class="step-desc">简单编辑后一键发布到各大社交平台</p>
        </div>
      </div>
    </section>

    <!-- 行动召唤 -->
    <section class="cta-section">
      <div class="cta-content">
        <h2 class="cta-title">准备好提升您的自媒体创作效率了吗？</h2>
        <p class="cta-subtitle">加入我们，与10,000+创作者一起成长</p>
        
        <!-- 添加信任标识区域 -->
        <div class="trust-signals">
          <div class="trust-text">受到行业领先者的信任</div>
          <div class="brands-container">
            <div class="brand-logo">科技媒体</div>
            <div class="brand-logo">内容工场</div>
            <div class="brand-logo">自媒体联盟</div>
            <div class="brand-logo">创作者协会</div>
          </div>
        </div>
        
        <!-- 添加核心优势简述 -->
        <div class="core-benefits">
          <div class="benefit-item">
            <el-icon class="benefit-icon"><Check /></el-icon>
            <span class="benefit-text">AI智能创作助手</span>
          </div>
          <div class="benefit-item">
            <el-icon class="benefit-icon"><Check /></el-icon>
            <span class="benefit-text">多平台一键分发</span>
          </div>
          <div class="benefit-item">
            <el-icon class="benefit-icon"><Check /></el-icon>
            <span class="benefit-text">数据分析仪表盘</span>
          </div>
        </div>
        
        <el-button type="primary" size="large" @click="goToTopicSelection" class="cta-button">
          免费开始使用
        </el-button>
      </div>
    </section>

    <!-- 页脚 -->
    <!-- 保留页面其他内容 -->
    <!-- 删除以下页脚部分 -->
    <!-- <el-footer class="footer"> ... </el-footer> -->
  </div>
</template>

<script>
import { PenTool, ArrowRight, PlayCircle, MagicStick, Layout, BarChart, Clock, Edit, RefreshLeft, Send, Weibo, Wechat, Github, Twitter } from '@element-plus/icons-vue';

export default {
  name: 'Introduction',
  data() {
    return {
      // 设置当前激活的菜单
      activeMenu: '1'
    };
  },
  components: {
    PenTool,
    ArrowRight,
    PlayCircle,
    MagicStick,
    Layout,
    BarChart,
    Clock,
    Edit,
    RefreshLeft,
    Send,
    Weibo,
    Wechat,
    Github,
    Twitter
  },
  methods: {
    goToTopicSelection() {
      this.$router.push('/topics');
    },
    goToLogin() {
      this.$router.push('/login');
    },
    goToRegister() {
      this.$router.push('/register');
    },
    // 确保新方法前有逗号
    goToHome() {
      this.$router.push('/');
      this.activeMenu = '1';
    },
    // 功能介绍路由（已存在）
    goToFeatures() {
      this.$router.push('/features');
      this.activeMenu = '2';
    },
    // 添加使用教程路由
    goToTutorial() {
      this.$router.push('/tutorial');
      this.activeMenu = '3';
    },
    // 添加关于我们路由
    goToAbout() {
      this.$router.push('/about');
      this.activeMenu = '4';
    },
    goToRegister() {
      this.$router.push('/register');
    }
  }
};
</script>

<style scoped lang="scss">
// 全局样式变量
$primary-color: #126bae;
$secondary-color: #35495e;
$light-gray: #f5f7fa;
$dark-gray: #4e5969;
$text-color: #333;
$text-light: #666;

.app-container {
  /* 移除 min-height: 100vh; 此行导致高度冲突 */
  display: flex;
  flex-direction: column;
}

// 导航栏样式
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2rem;
  height: 60px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;

  .logo {
    display: flex;
    align-items: center;
    gap: 8px;

    .logo-icon {
      color: $primary-color;
      font-size: 24px;
    }

    .logo-text {
      font-size: 20px;
      font-weight: bold;
      color: $secondary-color;
    }
  }

  .nav-menu {
    flex: 1;
    margin: 0 2rem;
  }

  .auth-buttons {
    display: flex;
    gap: 12px;

    .login-btn {
      color: $secondary-color;
    }

    .register-btn {
      background-color: $primary-color;
      border-color: $primary-color;
    }
  }
}

// 英雄区域样式
.hero-section {
  display: flex;
  align-items: center;
  padding: 4rem 2rem;
  background-color: $light-gray;
  gap: 2rem;

  .hero-content {
    flex: 1;

    .hero-title {
      font-size: 2.5rem;
      color: $secondary-color;
      margin-bottom: 1rem;
      line-height: 1.3;
    }

    .hero-subtitle {
      font-size: 1.2rem;
      color: $dark-gray;
      margin-bottom: 2rem;
      max-width: 600px;
    }

    .hero-actions {
      display: flex;
      gap: 1rem;
      margin-bottom: 3rem;

      .start-button {
        background-color: $primary-color;
        border-color: $primary-color;
        padding: 0.8rem 2rem;
      }

      .demo-button {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }

    .stats-container {
      display: flex;
      gap: 2rem;

      .stat-item {
        text-align: center;

        .stat-number {
          display: block;
          font-size: 1.8rem;
          font-weight: bold;
          color: $primary-color;
          margin-bottom: 0.3rem;
        }

        .stat-label {
          color: $text-light;
        }
      }
    }
  }

  .hero-image {
    flex: 1;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}

// 功能特点区域样式
.features-section {
  padding: 5rem 2rem;

  .section-header {
    text-align: center;
    margin-bottom: 3rem;

    .section-title {
      font-size: 2rem;
      color: $secondary-color;
      margin-bottom: 0.5rem;
    }

    .section-desc {
      color: $text-light;
      max-width: 700px;
      margin: 0 auto;
    }
  }

  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 2rem;
    max-width: 1200px;
    margin: 0 auto;

    .feature-card {
      padding: 2rem 1.5rem;
      border: none;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
      transition: transform 0.3s ease, box-shadow 0.3s ease;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
      }

      .feature-icon {
        width: 60px;
        height: 60px;
        background-color: rgba(66, 185, 131, 0.1);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 1.5rem;

        el-icon {
          color: $primary-color;
          font-size: 24px;
        }
      }

      .feature-title {
        font-size: 1.2rem;
        color: $secondary-color;
        margin-bottom: 0.8rem;
      }

      .feature-desc {
        color: $text-light;
        line-height: 1.6;
      }
    }
  }
}

// 使用流程区域样式
.process-section {
  padding: 5rem 2rem;
  background-color: $light-gray;

  .section-header {
    text-align: center;
    margin-bottom: 4rem;

    .section-title {
      font-size: 2rem;
      color: $secondary-color;
      margin-bottom: 0.5rem;
    }

    .section-desc {
      color: $text-light;
      max-width: 700px;
      margin: 0 auto;
    }
  }

  .process-steps {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 2rem;
    flex-wrap: wrap;

    .step-item {
      flex: 1;
      min-width: 200px;
      max-width: 300px;
      text-align: center;

      .step-number {
        width: 40px;
        height: 40px;
        background-color: $primary-color;
        color: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        margin: 0 auto 1rem;
      }

      .step-icon {
        width: 80px;
        height: 80px;
        background-color: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 1.5rem;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

        el-icon {
          color: $primary-color;
          font-size: 32px;
        }
      }

      .step-title {
        font-size: 1.2rem;
        color: $secondary-color;
        margin-bottom: 0.8rem;
      }

      .step-desc {
        color: $text-light;
      }
    }

    .step-connector {
      color: $primary-color;
      font-size: 24px;
      margin: 0 1rem;
    }
  }
}

// 行动召唤区域样式
.cta-section {
  padding: 4rem 2rem;
  text-align: center;
  background-color: $primary-color;
  color: white;
  margin-top: 3rem;
}

.trust-signals {
  margin: 2rem 0;
  padding: 1rem 0;
}

.trust-text {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 1rem;
}

.brands-container {
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
}

.brand-logo {
  background-color: rgba(255, 255, 255, 0.1);
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  font-size: 0.9rem;
}

.core-benefits {
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
  margin: 1.5rem 0 2.5rem;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: rgba(255, 255, 255, 0.9);
}

.benefit-icon {
  color: #FFD04B;
}

/* 修复：将游离样式归属于.features-grid容器 */
.features-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  padding: 0 1rem;
}

.feature-card {
  padding: 2rem 1.5rem;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }

  .feature-icon {
    width: 60px;
    height: 60px;
    background-color: rgba(66, 185, 131, 0.1);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 1.5rem;

    el-icon {
      color: $primary-color;
      font-size: 24px;
    }
  }

  .feature-title {
    font-size: 1.2rem;
    color: $secondary-color;
    margin-bottom: 0.8rem;
  }

  .feature-desc {
    color: $text-light;
    line-height: 1.6;
  }
}

// 使用流程区域样式
.process-section {
  padding: 5rem 2rem;
  background-color: $light-gray;

  .section-header {
    text-align: center;
    margin-bottom: 4rem;

    .section-title {
      font-size: 2rem;
      color: $secondary-color;
      margin-bottom: 0.5rem;
    }

    .section-desc {
      color: $text-light;
      max-width: 700px;
      margin: 0 auto;
    }
  }

  .process-steps {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 2rem;
    flex-wrap: wrap;

    .step-item {
      flex: 1;
      min-width: 200px;
      max-width: 300px;
      text-align: center;

      .step-number {
        width: 40px;
        height: 40px;
        background-color: $primary-color;
        color: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        margin: 0 auto 1rem;
      }

      .step-icon {
        width: 80px;
        height: 80px;
        background-color: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 1.5rem;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

        el-icon {
          color: $primary-color;
          font-size: 32px;
        }
      }

      .step-title {
        font-size: 1.2rem;
        color: $secondary-color;
        margin-bottom: 0.8rem;
      }

      .step-desc {
        color: $text-light;
      }
    }

    .step-connector {
      color: $primary-color;
      font-size: 24px;
      margin: 0 1rem;
    }
  }
}

// 行动召唤区域样式
.cta-section {
  padding: 6rem 2rem;
  background: linear-gradient(135deg, $primary-color 0%, #0a4b8c 100%);
  color: white;
  text-align: center;

  .cta-content {
    max-width: 800px;
    margin: 0 auto;

    .cta-title {
      font-size: 2.2rem;
      margin-bottom: 1rem;
    }

    .cta-subtitle {
      font-size: 1.2rem;
      opacity: 0.9;
      margin-bottom: 2.5rem;
    }

    .cta-button {
      padding: 1rem 3rem;
      font-size: 1.1rem;
      background-color: white;
      color: $primary-color;
      border: none;

      &:hover {
        background-color: #f5f5f5;
      }
    }
  }
}

// 页脚样式
.footer {
  background-color: $secondary-color;
  color: white;
  padding: 4rem 2rem 2rem;

  .footer-content {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    gap: 3rem;
    margin-bottom: 3rem;

    .footer-logo {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 1rem;

      .logo-icon {
        color: $primary-color;
        font-size: 24px;
      }

      .logo-text {
        font-size: 20px;
        font-weight: bold;
      }
    }

    .footer-links {
      display: flex;
      flex-wrap: wrap;
      gap: 4rem;

      .link-group {
        .link-title {
          font-size: 1rem;
          margin-bottom: 1rem;
          color: rgba(255, 255, 255, 0.8);
        }

        .footer-link {
          display: block;
          color: rgba(255, 255, 255, 0.6);
          margin-bottom: 0.5rem;
          text-decoration: none;

          &:hover {
            color: $primary-color;
          }
        }
      }
    }

    .footer-social {
      display: flex;
      gap: 1rem;

      .social-icon {
        font-size: 20px;
        color: rgba(255, 255, 255, 0.6);
        cursor: pointer;

        &:hover {
          color: $primary-color;
        }
      }
    }
  }

  .copyright {
    text-align: center;
    padding-top: 2rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    color: rgba(255, 255, 255, 0.4);
    font-size: 0.9rem;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .hero-section {
    flex-direction: column;
    text-align: center;

    .hero-content {
      .stats-container {
        justify-content: center;
      }
    }
  }

  .process-steps {
    flex-direction: column;

    .step-connector {
      transform: rotate(90deg);
      margin: 1rem 0;
    }
  }
}
</style>