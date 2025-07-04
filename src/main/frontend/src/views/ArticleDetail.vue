<template>
  <div class="article-detail-container">
    <el-card shadow="hover" class="detail-card">
      <div slot="header">
        <el-button @click="goBack" icon="ArrowLeft" class="back-button"></el-button>
      </div>

      <div class="article-header">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span class="author"><User /> {{ article.authorName }}</span>
          <span class="publish-date">{{ formatDate(article.createdAt) }}</span>
          <span class="view-count"><Eye /> {{ article.viewCount }} 阅读</span>
        </div>
      </div>

      <div class="article-content" v-html="article.content"></div>

      <div class="article-actions">
        <el-button type="text" icon="Like" @click="handleLike">{{ article.likeCount }} 点赞</el-button>
        <el-button type="text" icon="Share" @click="handleShare">分享</el-button>
        <el-button type="text" icon="Edit" @click="handleEdit" v-if="isAuthor">编辑</el-button>
      </div>

      <div class="comment-section">
        <h3>评论 ({{ article.commentCount }})</h3>
        <!-- 评论组件 -->
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ArrowLeft, User, Eye, Like, Share, Edit } from '@element-plus/icons-vue';
import request from '@/utils/request';
import { useStore } from 'vuex';
import { ElMessage } from 'element-plus'; // 添加此行

const router = useRouter();
const route = useRoute();
const store = useStore();
const article = ref({
  title: '加载中...',
  content: '',
  authorName: '未知作者',
  createdAt: new Date(),
  viewCount: 0,
  likeCount: 0,
  commentCount: 0
});
const articleId = ref(route.params.id);

// 获取文章详情
const fetchArticleDetail = async () => {
  try {
    const response = await request.get(`/articles/${articleId.value}`);
    console.log('文章详情响应:', response);
    
    // 修复数据访问层级 - 直接使用response而非response.data
    if (response) {
      // 后端返回格式为 {records: Array(1), total: 1, message: null}
      article.value = response.records?.[0] || response;
    } else {
      throw new Error('无效的响应格式');
    }
  } catch (error) {
    console.error('获取文章详情失败:', error);
    ElMessage.error(`加载文章失败: ${error.message || '未知错误'}`);
    // 可以添加默认错误内容显示
    article.value = {
      title: '文章未找到',
      content: '<div style="text-align:center;padding:40px;">无法加载文章内容，请检查文章ID是否正确</div>',
      authorName: '系统',
      createdAt: new Date(),
      viewCount: 0,
      likeCount: 0,
      commentCount: 0
    };
  }
};

// 格式化日期
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString();
};

// 判断是否为作者
const isAuthor = computed(() => {
  return store.state.user?.id === article.value.createdBy;
});

// 返回列表页
const goBack = () => {
  router.push('/articles');
};

// 点赞功能
const handleLike = async () => {
  try {
    await request.post(`/api/articles/${articleId.value}/like`);
    article.value.likeCount++;
    ElMessage.success('点赞成功');
  } catch (error) {
    ElMessage.error('点赞失败');
  }
};

// 分享功能
const handleShare = () => {
  // 实现分享逻辑
  ElMessage.info('分享功能已触发');
};

// 编辑文章
const handleEdit = () => {
  router.push(`/editor/${articleId.value}`);
};

onMounted(() => {
  fetchArticleDetail();
});
</script>

<style scoped>
.article-detail-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.back-button {
  margin-bottom: 20px;
}

.article-header {
  margin-bottom: 30px;
  text-align: center;
}

.article-title {
  font-size: 28px;
  margin-bottom: 15px;
}

.article-meta {
  display: flex;
  justify-content: center;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.article-content {
  line-height: 1.8;
  font-size: 16px;
  margin-bottom: 40px;
}

.article-actions {
  border-top: 1px solid #eee;
  padding-top: 20px;
  display: flex;
  gap: 20px;
}

.comment-section {
  margin-top: 40px;
  border-top: 1px solid #eee;
  padding-top: 20px;
}
</style>