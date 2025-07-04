<template>
  <div class="tutorial-container">
    <el-card class="tutorial-card">
      <div class="tutorial-header">
        <h1>使用教程</h1>
        <div class="divider"></div>
      </div>

      <div class="tutorial-content">
        <section class="tutorial-overview">
          <h2>平台使用指南</h2>
          <p>本教程将帮助您快速掌握MediaGenius智能自媒体内容创作平台的核心功能和操作流程。</p>
        </section>

        <section class="tutorial-steps">
          <div class="step-card">
            <div class="step-number">1</div>
            <div class="step-content">
              <h3>账号注册与登录</h3>
              <p>访问平台首页，点击右上角的"免费注册"按钮，填写相关信息完成注册。注册成功后使用账号密码登录系统。</p>
              <div class="step-image"></div>
            </div>
          </div>

          <div class="step-card">
            <div class="step-number">2</div>
            <div class="step-content">
              <h3>选题助手使用</h3>
              <p>登录后点击顶部导航栏的"选题助手"，输入关键词或选择领域，系统将智能生成相关选题建议。</p>
              <div class="step-image"></div>
            </div>
          </div>

          <div class="step-card">
            <div class="step-number">3</div>
            <div class="step-content">
              <h3>内容创作与编辑</h3>
              <p>选择合适的选题后，点击"创建内容"进入编辑器界面，使用AI辅助创作功能完成内容撰写。</p>
              <div class="step-image"></div>
            </div>
          </div>

          <div class="step-card">
            <div class="step-number">4</div>
            <div class="step-content">
              <h3>数据分析与优化</h3>
              <p>内容发布后，通过"数据看板"查看文章表现，根据数据分析结果优化后续创作策略。</p>
              <div class="step-image"></div>
            </div>
          </div>
        </section>

        <section class="tutorial-faq">
          <h2>常见问题</h2>
          <el-collapse>
            <el-collapse-item title="如何提高AI生成内容的质量？">
              <div>提供更具体的关键词和上下文描述，使用详细的创作指引，适当调整生成参数。</div>
            </el-collapse-item>
            <el-collapse-item title="内容可以导出哪些格式？">
              <div>目前支持导出为Word、PDF和Markdown格式，可在编辑器中点击"导出"按钮选择。</div>
            </el-collapse-item>
            <el-collapse-item title="如何查看历史创作记录？">
              <div>在"个人中心"页面中，点击"创作历史"选项卡即可查看所有历史内容。</div>
            </el-collapse-item>
          </el-collapse>
        </section>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElCollapse, ElCollapseItem } from 'element-plus';
</script>

<style scoped>
.tutorial-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.tutorial-header {
  text-align: center;
  margin-bottom: 40px;
}

.tutorial-header h1 {
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

.tutorial-content {
  line-height: 1.8;
  color: #606266;
}

.tutorial-content section {
  margin-bottom: 50px;
}

.tutorial-content h2 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eaeaea;
}

.tutorial-steps {
  margin-top: 30px;
}

.step-card {
  display: flex;
  margin-bottom: 40px;
  position: relative;
}

.step-number {
  width: 40px;
  height: 40px;
  background-color: #126bae;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  font-weight: bold;
  margin-right: 20px;
  flex-shrink: 0;
}

.step-content {
  flex-grow: 1;
}

.step-content h3 {
  font-size: 1.3rem;
  margin-bottom: 15px;
  color: #333;
}

.step-image {
  width: 100%;
  height: 200px;
  background-color: #f0f2f5;
  margin-top: 15px;
  border-radius: 4px;
}

.tutorial-faq {
  margin-top: 40px;
}

.el-collapse {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.el-collapse-item__header {
  font-size: 1.1rem;
}
</style>