<template>
  <div class="about-container">
    <el-card class="about-card">
      <div class="about-header">
        <h1>关于我们</h1>
        <div class="divider"></div>
      </div>

      <div class="about-content">
        <section class="company-intro">
          <h2>公司简介</h2>
          <p>MediaGenius 是一家专注于智能媒体内容创作的科技公司，致力于为自媒体创作者提供高效、智能的内容生成与管理解决方案。</p>
          <p>我们的使命是通过人工智能技术赋能内容创作者，帮助他们更快速地生产高质量内容，提升创作效率与影响力。</p>
        </section>

        <section class="team-intro">
          <h2>核心团队</h2>
          <div class="team-members">
            <div class="team-card">
              <div class="team-avatar"></div>
              <h3>张明</h3>
              <p>创始人 & CEO</p>
            </div>
            <div class="team-card">
              <div class="team-avatar"></div>
              <h3>李华</h3>
              <p>技术总监</p>
            </div>
            <div class="team-card">
              <div class="team-avatar"></div>
              <h3>王芳</h3>
              <p>产品经理</p>
            </div>
          </div>
        </section>

        <section class="contact-info">
          <h2>联系我们</h2>
          <ul>
            <li><i class="el-icon-location"></i> 成都市高新区天府大道123号创新中心A座15层</li>
            <li><i class="el-icon-phone"></i> 400-123-4567</li>
            <li><i class="el-icon-message"></i> contact@mediagenius.com</li>
          </ul>
        </section>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
</script>

<style scoped>
.about-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.about-header {
  text-align: center;
  margin-bottom: 40px;
}

.about-header h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 15px;
}

.divider {
  width: 100px;
  height: 4px;
  background-color: #126bae;
  margin: 0 auto;
}

.about-content {
  line-height: 1.8;
  color: #606266;
}

.about-content section {
  margin-bottom: 40px;
}

.about-content h2 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eaeaea;
}

.team-members {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}

.team-card {
  text-align: center;
  margin: 15px;
  width: 200px;
}

.team-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background-color: #f0f2f5;
  margin: 0 auto 15px;
}

.contact-info ul {
  list-style: none;
  padding: 0;
}

.contact-info li {
  margin-bottom: 15px;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
}

.contact-info i {
  margin-right: 10px;
  color: #126bae;
}
</style>