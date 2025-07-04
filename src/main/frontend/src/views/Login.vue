<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <div class="auth-header">
        <h2>账号登录</h2>
        <p>欢迎回来，请登录您的账号</p>
      </div>

      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名">
            <template #prefix><el-icon class="icon"><User /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码">
            <template #prefix><el-icon class="icon"><Lock /></el-icon></template>
          </el-input>
        </el-form-item>

        <div class="form-actions">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <router-link to="/forgot-password" class="forgot-password">忘记密码?</router-link>
        </div>

        <el-form-item>
          <el-button type="primary" class="login-button" @click="handleLogin" :loading="loading">
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-link">
        <span>还没有账号? </span>
        <router-link to="/register">立即注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<!-- 将<script setup>改为普通<script>标签 -->
<script>
import { User, Lock } from '@element-plus/icons-vue';
import request from '../utils/request';
import { ElMessage } from 'element-plus';
import router from '../router'; // 添加router导入

export default {
  name: 'Login',
  components: {
    User,
    Lock
  },
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ]
      },
      rememberMe: false,
      loading: false
    };
  },
  methods: {
    // 移除const关键字，修复语法错误
    handleLogin: async function() {
      this.loading = true;
      try {
        const response = await request.post('/auth/login', this.loginForm);
          console.log('登录响应数据:', response);
          
          // 尝试从多种可能的字段获取token
          const token = response.token || response.access_token || response.jwt || response.data?.token || response.data?.access_token;
          
          if (!token) {
            console.error('登录响应中未找到token字段');
            ElMessage.error('登录失败: 未获取到认证信息');
            this.loading = false;
            return;
          }
          
          localStorage.setItem('token', token);
          console.log('成功存储token:', token.substring(0, 10) + '...'); // 仅显示部分token确保安全
        ElMessage.success('登录成功');
        router.push('/dashboard');
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '登录失败');
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped lang="scss">
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  padding: 20px;
}

.auth-card {
  width: 100%;
  max-width: 400px;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.auth-header {
  text-align: center;
  padding: 2rem 0;
  background-color: #126bae;
  color: white;

  h2 {
    margin: 0 0 0.5rem;
    font-size: 1.8rem;
  }

  p {
    margin: 0;
    opacity: 0.9;
  }
}

.login-form {
  padding: 2rem;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.forgot-password {
  color: #126bae;
  text-decoration: none;
  font-size: 0.9rem;
}

.login-button {
  width: 100%;
  padding: 0.8rem;
  font-size: 1rem;
}

.register-link {
  text-align: center;
  padding: 1rem 2rem;
  border-top: 1px solid #eee;
  font-size: 0.9rem;

  a {
    color: #126bae;
    text-decoration: none;
    font-weight: 500;
  }
}
</style>