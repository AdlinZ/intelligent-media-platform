<template>
  <div class="article-list-container">
    <el-card shadow="hover">
      <!-- 添加调试信息显示 -->
      <!-- <div v-if="debugInfo" class="debug-info"> -->
        <!-- <p>调试信息: {{ debugInfo }}</p> -->
        <!-- <p>文章数据示例: {{ articles[0]?.title || '无数据' }}</p> 添加此行 -->
      <!-- </div> -->
      
      <!-- 原有代码保持不变 -->
      <div slot="header" class="clearfix">
        <h2 class="page-title">文章列表</h2>
        <el-button type="primary" @click="goToEditor">
          <Plus /> 新建文章
        </el-button>
      </div>

      <!-- 添加空状态显示 -->
      <div v-if="articles.length === 0 && !loading" class="empty-state">
        <p>没有找到文章数据</p>
      </div>

      <div v-else-if="loading" class="loading-state">
        <p>正在加载文章...</p>
      </div>

      <!-- 原有列表渲染代码 -->
      <!-- 替换el-list和el-list-item -->
      <div class="article-list">
        <div
          v-for="article in articles"
          :key="article.id"
          class="article-item"
          @click="goToDetail(article.id)"
        >
          <div class="article-card">
            <div class="article-header">
              <h3 class="article-title">{{ article.title }}</h3>
              <span class="publish-date">{{ formatDate(article.createdAt) }}</span>
            </div>
            <div class="article-meta">
              <span class="meta-item">👁️ {{ article.viewCount }} 阅读</span>
              <span class="meta-item">👍 {{ article.likeCount }} 点赞</span>
              <span class="meta-item">💬 {{ article.commentCount }} 评论</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页控件 -->
      <el-pagination
        v-if="total > 0"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        class="pagination"
        @current-change="handlePageChange"
      ></el-pagination>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { Plus, Search, Eye, Like, Comment } from '@element-plus/icons-vue';
// 添加缺失的组件导入
import { ElCard, ElInput, ElList, ElListItem, ElPagination } from 'element-plus';

// 注册图标组件
const components = {
  Plus,
  Search,
  Eye,
  Like,
  Comment,
  ElCard,
  ElInput,
  ElList,
  ElListItem,
  ElPagination
};
const router = useRouter();
const articles = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref('');
const loading = ref(false);
const debugInfo = ref('');

const fetchArticles = async () => {
  debugInfo.value = '开始执行fetchArticles';
  console.log('fetchArticles called');
  loading.value = true;
  try {
    // 添加认证检查
    const token = localStorage.getItem('token');
    if (!token) {
      debugInfo.value = '未检测到登录状态';
      ElMessage.warning('请先登录');
      router.push('/login');
      return;
    }

    debugInfo.value = '正在发送API请求';
    console.log('Sending request to /api/articles');
    const response = await request.get('/articles', { // 移除重复的/api前缀
      params: {
        page: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value
      }
    });
    debugInfo.value = 'API请求成功，处理响应数据';
    console.log('Response received:', response);
    articles.value = response.records || [];
    total.value = response.total || 0;
    debugInfo.value = `获取到${articles.value.length}篇文章`;
  } catch (error) {
    debugInfo.value = 'API请求失败: ' + (error.message || '未知错误');
    console.error('Error details:', error);
    articles.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 添加组件挂载日志
onMounted(() => {
  console.log('ArticleList component mounted');
  debugInfo.value = '组件已挂载，准备获取文章列表';
  fetchArticles();
});

// 添加日期格式化函数
const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleString();
};

// 添加文章详情页导航方法
const goToDetail = (articleId) => {
  router.push(`/articles/${articleId}`);
};

// 添加新建文章导航方法
const goToEditor = () => {
  router.push('/editor');
};

// 添加分页处理方法
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchArticles();
};
</script>

<style scoped>
.article-list-container {
  padding: 20px;
}

.page-title {
  display: inline-block;
  margin-right: 20px;
}

.search-input {
  margin-bottom: 20px;
  max-width: 500px;
}

.article-item {
  cursor: pointer;
  padding: 10px 0;
}

.article-card {
  width: 100%;
  transition: transform 0.2s;
}

.article-card:hover {
  transform: translateY(-5px);
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.article-title {
  margin: 0;
  font-size: 18px;
  color: #333; /* 添加文字颜色 */
  padding: 5px 0; /* 添加内边距 */
}

.publish-date {
  color: #666;
  font-size: 14px;
}

.article-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

/* 添加调试样式 */
.debug-info {
  background-color: #fff3cd;
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 4px;
}

.empty-state,
.loading-state {
  text-align: center;
  padding: 40px 0;
  color: #666;
}
</style>