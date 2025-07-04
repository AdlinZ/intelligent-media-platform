<template>
  <div class="content-editor-container">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <el-button @click="goBack" icon="ArrowLeft" class="back-button">返回选题</el-button>
        <h2 class="editor-title">内容编辑器</h2>
      </div>

      <el-form ref="formRef" :model="form" class="content-form">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入内容标题" class="title-input"></el-input>
        </el-form-item>

        <el-form-item label="平台类型">
          <el-radio-group v-model="platformType" @change="convertContent">
            <el-radio-button label="default">默认格式</el-radio-button>
            <el-radio-button label="wechat">公众号</el-radio-button>
            <el-radio-button label="douyin">抖音脚本</el-radio-button>
            <el-radio-button label="xiaohongshu">小红书</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="内容编辑">
          <!-- 移除ref属性，仅保留v-model绑定 -->
          <QuillEditor
            ref="quillEditor"
            v-model:content="form.content"
            class="editor"
            :options="editorOptions"
            @update:content="handleContentChange"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="saveContent" :loading="saving">保存内容</el-button>
          <el-button @click="generateContent" :loading="generating">AI生成内容</el-button>
          <el-button @click="analyzeContent" class="ml-2">内容分析</el-button>
          <el-button @click="goToDashboard" class="ml-2" type="success">查看数据</el-button>
          <el-button @click="viewLastArticle" class="ml-2" type="info" :disabled="!lastSavedArticleId">查看文章</el-button>
          <el-button @click="testSave" class="ml-2" type="warning">测试保存</el-button>
          <el-button @click="quickLogin" class="ml-2" type="info">快速登录</el-button>
          <el-button @click="testSetContent" class="ml-2" type="secondary">测试设置内容</el-button>
          <el-button @click="debugContent" class="ml-2" type="danger">调试内容</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 内容分析结果弹窗 -->
    <el-dialog v-model="analysisDialogVisible" title="内容优化建议" width="600px">
      <div v-for="(suggestion, index) in analysisSuggestions" :key="index" class="suggestion-item">
        <el-alert
          :title="suggestion.title"
          :description="suggestion.content"
          :type="suggestion.type"
          show-icon
        ></el-alert>
      </div>
      <template #footer>
        <el-button @click="analysisDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import axios from 'axios'
import { QuillEditor } from '@vueup/vue-quill'
import 'quill/dist/quill.snow.css'
import { ElMessage, ElDialog, ElAlert } from 'element-plus'
import { generateContent as generateContentApi, saveContent as saveContentApi } from '../api/topic'
import { marked } from 'marked';
// 添加HTML净化库
import DOMPurify from 'dompurify';
import request from '../utils/request';

// 路由和状态管理
const route = useRoute()
const router = useRouter()

// 表单数据
const form = reactive({
  title: '',
  content: ''
})

// 状态变量
const platformType = ref('default')
const analysisDialogVisible = ref(false)
const analysisSuggestions = ref([])
const generating = ref(false)
const saving = ref(false)
const formRef = ref(null)
const quillEditor = ref(null)

// 防止内容变化事件干扰的标志
const isSettingContent = ref(false)

// 最后保存的文章ID
const lastSavedArticleId = ref(null)

// 富文本编辑器配置
const editorOptions = {
  theme: 'snow',
  modules: {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      [{ 'header': [1, 2, 3, false] }],
      [{ 'list': 'ordered'}, { 'list': 'bullet' }],
      [{ 'align': [] }],
      ['link', 'image'], // 确保包含image模块
      ['clean']
    ],
    clipboard: {
      matchVisual: false
    }
  },
  // 添加安全策略配置
  sanitize: false,
  placeholder: '请输入内容...'
};


// 导航方法
const goBack = () => router.push('/')
const goToDashboard = () => router.push('/dashboard')

// 辅助方法
const getTopic = () => {
  const topic = route.params.topicId || route.query.keyword || '';
  // 添加调试日志
  console.log('获取到的选题:', topic);
  return topic || '默认标题'; // 提供默认值防止为空
};
const getDifficulty = () => route.query.difficulty || 'medium'

// AI生成内容
const generateContent = async () => {
  const topic = getTopic();
  if (!topic) {
    ElMessage.warning('未找到选题，无法生成内容');
    return;
  }

  generating.value = true;
  try {
    form.title = topic;
    console.log('开始生成内容，选题:', topic);
    
    const response = await generateContentApi({
      keyword: topic,
      difficulty: getDifficulty(),
      wordCount: 300
    });

    console.log('API响应:', response);

    if (!response?.content) {
      throw new Error('API返回内容为空');
    }

    console.log('原始内容:', response.content);

    // 如果内容是纯文本，转换为HTML
    let htmlContent;
    if (response.content.includes('<') && response.content.includes('>')) {
      // 已经是HTML格式
      htmlContent = response.content;
    } else {
      // 纯文本，转换为HTML
      htmlContent = marked.parse(response.content);
    }
    
    console.log('转换后的HTML:', htmlContent);

    // 净化HTML内容
    const sanitizedHtml = DOMPurify.sanitize(htmlContent);
    console.log('净化后的HTML:', sanitizedHtml);
    
    // 验证HTML内容
    if (typeof sanitizedHtml !== 'string' || sanitizedHtml.trim().length < 10) {
      throw new Error('生成的HTML内容无效');
    }

    // 设置标志，防止内容变化事件干扰
    isSettingContent.value = true;
    
    // 先设置到form.content
    form.content = sanitizedHtml;
    console.log('设置到form.content:', form.content);
    
    // 强制更新编辑器
    await nextTick();
    
    // 手动设置编辑器内容 - 使用setContents方法
    if (quillEditor.value && quillEditor.value.setContents) {
      try {
        // 将HTML转换为Delta格式
        const delta = quillEditor.value.clipboard.convert(sanitizedHtml);
        quillEditor.value.setContents(delta);
        console.log('使用setContents设置编辑器内容成功');
      } catch (error) {
        console.log('setContents方法失败:', error);
        // 尝试其他方法
        if (quillEditor.value && quillEditor.value.setHTML) {
          try {
            quillEditor.value.setHTML(sanitizedHtml);
            console.log('使用setHTML设置编辑器内容成功');
          } catch (error2) {
            console.log('setHTML方法也失败:', error2);
            // 最后尝试直接设置innerHTML
            if (quillEditor.value && quillEditor.value.root) {
              try {
                quillEditor.value.root.innerHTML = sanitizedHtml;
                console.log('直接设置innerHTML成功');
              } catch (error3) {
                console.log('所有设置方法都失败了:', error3);
              }
            }
          }
        }
      }
    } else if (quillEditor.value && quillEditor.value.setHTML) {
      try {
        quillEditor.value.setHTML(sanitizedHtml);
        console.log('使用setHTML设置编辑器内容成功');
      } catch (error) {
        console.log('setHTML方法失败:', error);
        // 尝试直接设置innerHTML
        if (quillEditor.value && quillEditor.value.root) {
          try {
            quillEditor.value.root.innerHTML = sanitizedHtml;
            console.log('直接设置innerHTML成功');
          } catch (error2) {
            console.log('直接设置innerHTML也失败:', error2);
          }
        }
      }
    } else {
      console.log('编辑器实例不存在或没有设置内容的方法');
    }
    
    // 延迟重置标志
    setTimeout(() => {
      isSettingContent.value = false;
      console.log('重置内容设置标志');
    }, 100);
    
    // 再次验证内容已正确设置
    setTimeout(() => {
      console.log('延迟检查 - form.content类型:', typeof form.content);
      
      // 获取实际的HTML内容
      let actualContent = form.content;
      if (quillEditor.value && quillEditor.value.getHTML) {
        actualContent = quillEditor.value.getHTML();
      }
      
      console.log('延迟检查 - 实际内容长度:', actualContent.length);
      console.log('延迟检查 - 实际内容预览:', actualContent.substring(0, 100));
      
      if (actualContent.length < 20) { // 调整长度检查
        console.error('内容未正确设置到编辑器');
        ElMessage.warning('内容生成成功但显示异常，请检查编辑器');
      } else {
        ElMessage.success('内容生成成功');
      }
    }, 200); // 增加延迟时间
    
  } catch (error) {
    ElMessage.error(`内容生成失败: ${error.message || '未知错误'}`);
    console.error('生成内容错误:', error);
  } finally {
    generating.value = false;
  }
};

// 内容格式转换
const convertContent = () => {
  if (!form.content) return;

  let processedContent = form.content;
  switch(platformType.value) {
    case 'wechat':
      processedContent = form.content
        .replace(/<h2>(.*?)<\/h2>/g, '<h2 class="wechat-title">$1</h2>')
        .replace(/<p>/g, '<p class="wechat-paragraph">');
      break;
    case 'douyin':
      processedContent = form.content
        .replace(/<p>/g, '<p>[字幕]')
        .replace(/<\/p>/g, '</p>\n[转场]')
        .replace(/<strong>(.*?)<\/strong>/g, '[重点]$1[/重点]');
      break;
    case 'xiaohongshu':
      const topicTags = form.title
        .split(/[，。 、]/)
        .filter(word => word.length > 2)
        .slice(0, 3)
        .map(word => `#${word}`)
        .join(' ');

      processedContent = form.content
        .replace(/<p>/g, '<p>✨ ')
        .replace(/<\/p>/g, '</p>')
        + `<p>${topicTags}</p>`;
      break;
    default:
      // 默认格式不做处理
      break;
  }
  form.content = processedContent;
};

// 保存内容
const saveContent = async () => {
  // 获取编辑器的实际HTML内容
  let contentToSave = form.content;
  
  // 如果form.content是对象，尝试获取HTML
  if (typeof form.content === 'object' && quillEditor.value && quillEditor.value.getHTML) {
    contentToSave = quillEditor.value.getHTML();
    console.log('从编辑器获取HTML内容:', contentToSave);
  }
  
  // 修复类型检查
  if (!form.title || !contentToSave || typeof form.title !== 'string' || typeof contentToSave !== 'string') {
    ElMessage.warning('标题和内容不能为空或格式错误');
    console.error('内容类型错误:', { 
      titleType: typeof form.title, 
      contentType: typeof contentToSave,
      title: form.title,
      content: contentToSave
    });
    return;
  }

  // 确保内容不为空
  if (form.title.trim() === '' || contentToSave.trim() === '') {
    ElMessage.warning('标题和内容不能为空');
    return;
  }

  // 检查登录状态
  if (!checkLoginStatus()) return;

  saving.value = true;
  try {
    const topic = getTopic();
    const response = await saveContentApi({
      title: form.title,
      content: contentToSave,
      keyword: topic,
      difficulty: getDifficulty(),
      wordCount: 300
    });

    if (response.success && response.id) {
      lastSavedArticleId.value = response.id;
      ElMessage.success('内容保存成功');
      ElMessage.info('点击"查看文章"按钮查看保存的内容');
    } else {
      ElMessage.error(response.warning || '内容保存失败');
    }
  } catch (error) {
    ElMessage.error(`内容保存接口调用失败: ${error.message || '网络错误'}`);
  } finally {
    saving.value = false;
  }
};

// 内容分析
const analyzeContent = async () => {
  // 获取编辑器的实际HTML内容
  let contentToAnalyze = form.content;
  
  // 如果form.content是对象，尝试获取HTML
  if (typeof form.content === 'object' && quillEditor.value && quillEditor.value.getHTML) {
    contentToAnalyze = quillEditor.value.getHTML();
  }
  
  // 修复类型检查
  if (typeof form.title !== 'string' || typeof contentToAnalyze !== 'string' || !form.title.trim() || !contentToAnalyze.trim()) {
    ElMessage.warning('请先生成或编辑有效的内容');
    console.error('内容类型错误:', { 
      titleType: typeof form.title, 
      contentType: typeof contentToAnalyze,
      title: form.title,
      content: contentToAnalyze
    });
    return;
  }

  try {
    ElMessage.loading('AI正在分析内容...');
    const response = await axios.post('/api/ai/analyze', {
      title: form.title,
      content: contentToAnalyze,
      platformType: platformType.value
    });

    ElMessage.closeAll();
    if (response.data.success) {
      analysisSuggestions.value = response.data.data.suggestions;
      analysisDialogVisible.value = true;
    } else {
      ElMessage.error(`分析失败: ${response.data.message}`);
    }
  } catch (error) {
    ElMessage.closeAll();
    ElMessage.error(`AI分析接口调用失败: ${error.message}`);
  }
};

// 测试保存功能
const testSave = async () => {
  try {
    const response = await request.post('/content/test-save');
    if (response.success) {
      ElMessage.success(`测试保存成功，ID: ${response.id}`);
    } else {
      ElMessage.error(`测试保存失败: ${response.warning}`);
    }
  } catch (error) {
    ElMessage.error(`测试保存接口调用失败: ${error.message}`);
  }
};

// 快速登录测试
const quickLogin = async () => {
  try {
    const response = await request.post('/auth/login', {
      username: 'admin',
      password: 'admin123'
    });
    
    if (response.token) {
      localStorage.setItem('token', response.token);
      localStorage.setItem('tokenType', response.tokenType || 'Bearer');
      ElMessage.success('快速登录成功');
      console.log('登录成功，token:', response.token);
    } else {
      ElMessage.error('快速登录失败');
    }
  } catch (error) {
    ElMessage.error(`快速登录失败: ${error.message}`);
  }
};

// 测试设置编辑器内容
const testSetContent = () => {
  const testContent = '<h2>测试标题</h2><p>这是一个测试内容，用于验证编辑器是否正常工作。</p><p>如果您能看到这段内容，说明编辑器功能正常。</p>';
  
  console.log('测试设置内容:', testContent);
  form.content = testContent;
  
  // 强制更新编辑器
  nextTick(() => {
    // 方法1: 使用setContents方法
    if (quillEditor.value && quillEditor.value.setContents) {
      try {
        const delta = quillEditor.value.clipboard.convert(testContent);
        quillEditor.value.setContents(delta);
        console.log('使用setContents设置测试内容成功');
        ElMessage.success('测试内容设置成功');
        return;
      } catch (error) {
        console.log('setContents方法失败:', error);
      }
    }
    
    // 方法2: 使用setHTML方法
    if (quillEditor.value && quillEditor.value.setHTML) {
      try {
        quillEditor.value.setHTML(testContent);
        console.log('使用setHTML设置测试内容成功');
        ElMessage.success('测试内容设置成功');
        return;
      } catch (error) {
        console.log('setHTML方法失败:', error);
      }
    }
    
    // 方法3: 直接设置innerHTML
    if (quillEditor.value && quillEditor.value.root) {
      try {
        quillEditor.value.root.innerHTML = testContent;
        console.log('直接设置innerHTML成功');
        ElMessage.success('测试内容设置成功');
        return;
      } catch (error) {
        console.log('直接设置innerHTML失败:', error);
      }
    }
    
    console.log('所有设置方法都失败了');
    ElMessage.warning('编辑器实例不存在或设置失败');
  });
};

// 检查登录状态
const checkLoginStatus = () => {
  const token = localStorage.getItem('token');
  if (!token) {
    ElMessage.warning('请先登录后再保存内容');
    router.push('/login');
    return false;
  }
  return true;
};

// 查看文章
const viewLastArticle = () => {
  if (lastSavedArticleId.value) {
    router.push(`/articles/${lastSavedArticleId.value}`);
  } else {
    ElMessage.warning('还没有保存的文章');
  }
};

// 页面加载时处理
onMounted(() => {
  // 检查登录状态
  const token = localStorage.getItem('token');
  console.log('当前登录状态:', token ? '已登录' : '未登录');
  console.log('Token:', token);

  // 添加新的初始化逻辑
  const topic = getTopic();
  if (topic) {
    form.title = topic;
    console.log('初始化标题:', form.title);
  }
});
const handleContentChange = (content) => {
  // 如果正在设置内容，忽略变化事件
  if (isSettingContent.value) {
    console.log('忽略内容变化事件 - 正在设置内容');
    return;
  }
  
  console.log('编辑器内容变化:', content);
  console.log('内容长度:', content.length);
  console.log('内容类型:', typeof content);
  
  // 如果content是对象（Quill的Delta格式），需要转换为HTML
  if (typeof content === 'object' && content.ops) {
    // 这是一个Quill Delta对象，需要转换为HTML
    if (quillEditor.value && quillEditor.value.getHTML) {
      const htmlContent = quillEditor.value.getHTML();
      console.log('转换为HTML:', htmlContent);
      form.content = htmlContent;
    } else {
      // 如果没有getHTML方法，尝试转换为字符串
      form.content = JSON.stringify(content);
    }
  } else if (typeof content === 'string') {
    form.content = content;
  } else {
    console.warn('未知的内容格式:', content);
    form.content = String(content);
  }
};

// 调试内容
const debugContent = () => {
  console.log('=== 调试内容信息 ===');
  console.log('form.content:', form.content);
  console.log('form.content类型:', typeof form.content);
  console.log('form.title:', form.title);
  console.log('form.title类型:', typeof form.title);
  
  // 检查QuillEditor实例
  console.log('quillEditor.value:', quillEditor.value);
  if (quillEditor.value) {
    console.log('quillEditor.value类型:', typeof quillEditor.value);
    console.log('quillEditor.value方法:', Object.getOwnPropertyNames(quillEditor.value));
    console.log('quillEditor.value.root:', quillEditor.value.root);
    
    // 尝试获取编辑器的HTML内容
    if (quillEditor.value.getHTML) {
      const htmlContent = quillEditor.value.getHTML();
      console.log('编辑器HTML内容:', htmlContent);
      console.log('HTML内容长度:', htmlContent.length);
    }
    
    // 尝试获取编辑器的文本内容
    if (quillEditor.value.getText) {
      const textContent = quillEditor.value.getText();
      console.log('编辑器文本内容:', textContent);
      console.log('文本内容长度:', textContent.length);
    }
    
    // 检查root元素
    if (quillEditor.value.root) {
      console.log('root.innerHTML:', quillEditor.value.root.innerHTML);
      console.log('root.textContent:', quillEditor.value.root.textContent);
    }
  } else {
    console.log('quillEditor.value不存在');
  }
  
  console.log('=== 调试结束 ===');
};
</script>

<style scoped>
.content-editor-container { padding: 20px; }
.editor-title { text-align: center; margin: 0; }
.back-button { margin-right: 10px; }
.content-form { margin-top: 20px; }
.title-input { width: 100%; min-height: 40px; }
/* 修复编辑器高度问题 */
.editor { height: 500px !important; border: 1px solid #e5e7eb; }
.suggestion-item { margin-bottom: 15px; }

/* 修复深度选择器语法 */
:deep(.ql-container) { height: calc(100% - 42px) !important; width: 100% !important; }
:deep(.ql-editor) { min-height: 400px; font-size: 16px; line-height: 1.6; }
:deep(.ql-toolbar) { width: 100% !important; }
.quill-editor { display: flex; flex-direction: column; width: 100% !important; }
</style>

<!-- 删除多余的空样式块 -->
<!-- <style>
/* 移除重复的编辑器高度样式 */
/* .quill-editor { height: 400px; } */
</style> -->