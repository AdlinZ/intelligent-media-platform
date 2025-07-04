<template>
  <div class="topic-selection-container">
    <el-card class="topic-card" shadow="hover">
      <div slot="header" class="clearfix">
        <h2>智能选题助手</h2>
      </div>
      <el-form ref="form" :model="form" label-width="100px" class="topic-form">
        <el-form-item label="领域关键词">
          <el-input v-model="form.keyword" placeholder="请输入内容领域，如'美食'、'旅行'" @keyup.enter.native="generateTopics"></el-input>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="form.difficulty" placeholder="选择难度" class="difficulty-select">
            <el-option label="简单" value="easy"></el-option>
            <el-option label="中等" value="medium"></el-option>
            <el-option label="困难" value="hard"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="generateTopics" :loading="loading">生成选题</el-button>
        </el-form-item>
      </el-form>
      <div v-if="topics.length > 0" class="topics-result">
        <h3>为您推荐的选题：</h3>
        <el-list>
          <el-list-item v-for="(topic, index) in topics" :key="index" class="topic-item" @click="goToEditor(topic)">
            <el-icon><fire-filled /></el-icon>
            {{ topic }}
          </el-list-item>
        </el-list>
      </div>
      <div v-else-if="showNoResult" class="no-result">
        <el-empty description="暂无选题，请更换关键词或稍后重试"></el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { FireFilled } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { generateTopics as generateTopicsApi } from '../api/topic';

const router = useRouter();
const form = reactive({
  keyword: '',
  difficulty: 'medium',
});
const topics = ref([]);
const loading = ref(false);
const showNoResult = ref(false);

function splitTopics(rawTopics) {
  if (Array.isArray(rawTopics)) {
    // 已经是数组
    return rawTopics.flatMap(t => splitTopics(t));
  }
  if (typeof rawTopics === 'string') {
    // 按逗号、换行、分号等分割
    return rawTopics
      .split(/[，,;；\n\r]+/)
      .map(t => t.trim())
      .filter(Boolean);
  }
  return [];
}

const generateTopics = async () => {
  if (!form.keyword.trim()) {
    ElMessage.warning('请输入关键词');
    return;
  }
  loading.value = true;
  try {
    const response = await generateTopicsApi({
      keyword: form.keyword,
      difficulty: form.difficulty,
      language: 'zh-CN',
    });
    if (response.success && response.topics && response.topics.length > 0) {
      // 处理为一行一个选题
      topics.value = splitTopics(response.topics);
      showNoResult.value = false;
    } else {
      topics.value = [];
      showNoResult.value = true;
      ElMessage.error(response.message || '未能生成选题，请更换关键词或稍后重试');
    }
  } catch (error) {
    topics.value = [];
    showNoResult.value = true;
    ElMessage.error('生成选题失败: ' + (error.message || '网络错误'));
  } finally {
    loading.value = false;
  }
};

const goToEditor = (topic) => {
  router.push({
    name: 'ContentEditor',
    params: {
      topicId: topic,  // 修改为params传递并匹配路由参数名
    },
    query: {
      difficulty: form.difficulty,  // 难度参数仍可保留为query
    },
  });
};
</script>

<style scoped>
.topic-selection-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}
.topic-card {
  border-radius: 12px;
  overflow: hidden;
}
/* 添加标题居中样式 */
.clearfix {
  text-align: center;
  margin-bottom: 20px;
}
.clearfix h2 {
  margin: 0;
  display: inline-block;
}
.topic-form {
  margin-bottom: 30px;
}
.difficulty-select {
  width: 120px;
}
.topics-result {
  margin-top: 20px;
}
.topic-item {
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}
.topic-item:hover {
  background-color: #f5f7fa;
}
.no-result {
  margin-top: 40px;
  padding: 40px 0;
}
.el-collapse-item__content {
  padding-left: 20px;  /* 添加左侧缩进 */
}

/* 或者针对具体内容元素 */
.el-collapse-item__content > div {
  padding-left: 20px;
  margin-bottom: 10px;
}
</style>