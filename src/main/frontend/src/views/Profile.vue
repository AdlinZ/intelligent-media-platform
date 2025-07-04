<template>
  <div class="profile-container">
    <el-card class="profile-card" shadow="hover">
      <div slot="header" class="clearfix">
        <h2>个人中心</h2>
      </div>

      <div class="profile-content">
        <!-- 用户信息概览 -->
        <div class="profile-overview">
          <div class="avatar-container">
            <el-avatar :size="100" class="user-avatar">
            <img v-if="imageUrl" :src="imageUrl" alt="User Avatar" />
            <span v-else>{{ userInitials }}</span>
          </el-avatar>
            <el-button type="primary" size="small" class="edit-avatar-btn" @click="openAvatarDialog">更换头像</el-button>

<el-dialog v-model="avatarDialogVisible" title="更换头像" width="400px" :append-to-body="true">
  <el-upload
    class="avatar-uploader"
    action="/api/avatar/upload"
    :show-file-list="false"
    :on-success="handleAvatarSuccess"
    :on-error="handleAvatarError"
    :before-upload="beforeAvatarUpload"
    :headers="authHeader"
  >
    <img v-if="imageUrl" :src="imageUrl" class="avatar" />
    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
  </el-upload>
  <template #footer>
    <el-button @click="avatarDialogVisible = false">取消</el-button>
  </template>
</el-dialog>
          </div>

          <div class="user-info">
            <h3>{{ username }}</h3>
            <p class="user-email">{{ email }}</p>
            <p class="user-joined">注册时间: {{ formattedJoinDate }}</p>
          </div>
        </div>

        <el-divider></el-divider>

        <!-- 账户设置 -->
        <div class="account-settings">
          <h3>账户设置</h3>
          <el-form ref="settingsForm" :model="settingsForm" label-width="120px" class="settings-form">
            <el-form-item label="用户名">
              <el-input v-model="settingsForm.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item label="电子邮箱">
              <el-input v-model="settingsForm.email" type="email" placeholder="请输入电子邮箱"></el-input>
            </el-form-item>
            <el-form-item label="手机号码">
              <el-input v-model="settingsForm.phone" placeholder="请输入手机号码"></el-input>
            </el-form-item>
            <el-form-item label="旧密码">
              <el-input v-model="settingsForm.oldPassword" type="password" placeholder="请输入旧密码"></el-input>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="settingsForm.newPassword" type="password" placeholder="请输入新密码"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSettings">保存修改</el-button>
              <el-button @click="resetForm">取消</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { Plus } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import request from '../utils/request';
import { ElMessage, ElLoading } from 'element-plus';

const authHeader = computed(() => {
  if (typeof window !== 'undefined' && window.localStorage) {
    const token = window.localStorage.getItem('token');
    return token ? { Authorization: `Bearer ${token}` } : {};
  }
  return {};
});

const router = useRouter();
const loading = ref(false);

// 用户信息数据
const username = ref('');
const email = ref('');
const joinDate = ref('');
const userInitials = computed(() => {
  return username.value ? username.value.charAt(0).toUpperCase() : 'U';
});
const formattedJoinDate = computed(() => {
  return joinDate.value ? new Date(joinDate.value).toLocaleDateString() : '';
});

// 设置表单数据
const settingsForm = reactive({
  username: '',
  email: '',
  phone: '',
  oldPassword: '',
  newPassword: ''
});

// 获取用户信息
const getUserProfile = async () => {
  const loadingInstance = ElLoading.service({ text: '加载中...' });
  try {
    console.log('开始请求用户数据...');
    const response = await request.get('/api/auth/me');
    console.log('用户数据请求成功:', response);
    const userData = response.data || {};
console.log('完整用户数据:', userData);
console.log('头像URL:', userData.avatarUrl);
console.log('原始注册时间:', userData.joinDate, userData.createdAt);
username.value = userData.username || '';
email.value = userData.email || '';
joinDate.value = userData.joinDate || userData.createdAt || '';
console.log('处理后的注册时间:', joinDate.value);
// 加载用户头像URL
if (userData.avatarUrl) {
  imageUrl.value = userData.avatarUrl;
  console.log('Loaded avatar URL:', imageUrl.value);
}

    // 初始化表单数据
    settingsForm.username = username.value;
    settingsForm.email = email.value;
    // 修复手机号显示邮箱问题：如果phone与email相同则清空
    // 增强手机号校验：排除邮箱格式内容并确保与邮箱不同
    // 多重校验确保手机号字段不显示邮箱
    // 最终防护：确保手机号字段绝不会显示邮箱
    // 添加调试日志
    console.log('Backend phone value:', userData.phone);
    const phoneValue = (userData.phone || '').trim();
    const emailValue = settingsForm.email.trim().toLowerCase();
    
    // 多重校验：空值、邮箱格式、与邮箱相同（大小写不敏感）
    if (!phoneValue || phoneValue.includes('@') || phoneValue.toLowerCase() === emailValue) {
      settingsForm.phone = '';
    } else {
      settingsForm.phone = phoneValue;
    }
  } catch (error) {
    console.error('用户数据请求失败详情:', error);
    console.error('响应状态:', error.response?.status);
    console.error('响应数据:', error.response?.data);
    ElMessage.error(`获取用户信息失败: ${error.response?.data?.message || error.message}`);
    // 仅在确认认证失败时才重定向
    if (error.response?.status === 401 || error.response?.status === 403) {
      router.push('/login');
    }
  } finally {
    loadingInstance.close();
  }
};

// 保存设置
const saveSettings = async () => {
  if (!settingsForm.username.trim()) {
    ElMessage.warning('用户名不能为空');
    return;
  }

  if (!settingsForm.email.trim()) {
    ElMessage.warning('电子邮箱不能为空');
    return;
  }

  loading.value = true;
  try {
    await request.put('/auth/profile', {
      username: settingsForm.username,
      email: settingsForm.email,
      phone: settingsForm.phone,
      oldPassword: settingsForm.oldPassword,
      newPassword: settingsForm.newPassword
    });

    ElMessage.success('个人信息更新成功');
    username.value = settingsForm.username;
    email.value = settingsForm.email;

    // 清除密码字段
    settingsForm.oldPassword = '';
    settingsForm.newPassword = '';
  } catch (error) {
    ElMessage.error(`更新失败: ${error.response?.data?.message || error.message}`);
    console.error('Error saving profile settings:', error);
  } finally {
    loading.value = false;
  }
};

// 重置表单
const resetForm = () => {
  settingsForm.username = username.value;
  settingsForm.email = email.value;
  settingsForm.oldPassword = '';
  settingsForm.newPassword = '';
};

// 头像上传成功处理
const handleAvatarSuccess = (response) => {
  if (response && response.avatarUrl) {
    imageUrl.value = response.avatarUrl;
    ElMessage.success('头像上传成功');
    avatarDialogVisible.value = false;
    // 重新获取用户信息以更新头像显示
    getUserProfile();
  } else {
    ElMessage.error('头像上传失败: 无效响应');
  }
};

// 头像上传错误处理
const handleAvatarError = (error) => {
  ElMessage.error(`头像上传失败: ${error.message || '网络错误'}`);
};

// 头像预览URL
const imageUrl = ref('');
const avatarDialogVisible = ref(false);

const openAvatarDialog = () => {
  console.log('Opening avatar dialog, current visibility:', avatarDialogVisible.value);
  avatarDialogVisible.value = true;
  console.log('After setting to true:', avatarDialogVisible.value);
};

// 页面加载时获取用户信息
onMounted(() => {
  getUserProfile();
});
</script>

<style scoped>
.profile-container {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 20px;
}

.profile-card {
  border-radius: 12px;
  overflow: hidden;
}

.profile-content {
  padding: 20px;
}

.profile-overview {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.avatar-container {
  margin-right: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.user-avatar {
  background-color: #126bae;
  font-size: 40px;
  margin-bottom: 10px;
}

:deep .el-dialog {
  z-index: 3000 !important;
}

:deep .el-dialog__wrapper {
  z-index: 3000 !important;
}

.edit-avatar-btn {
  width: 100%;
}

.user-info h3 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #333;
}

.user-email {
  color: #606266;
  margin: 0 0 5px 0;
}

.user-joined {
  color: #909399;
  margin: 0;
  font-size: 14px;
}

.account-settings h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
}

.settings-form {
  max-width: 600px;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>