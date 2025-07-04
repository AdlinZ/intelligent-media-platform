<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <div class="auth-header">
        <h2>账号注册</h2>
        <p>创建新账号，开始您的创作之旅</p>
      </div>

      <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名">
            <template #prefix><el-icon class="icon"><User /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email">
          <el-input v-model="registerForm.email" type="email" placeholder="请输入邮箱">
            <template #prefix><el-icon class="icon"><Message /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码">
            <template #prefix><el-icon class="icon"><Lock /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码">
            <template #prefix><el-icon class="icon"><Lock /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="register-button" @click="handleRegister" :loading="loading">
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-link">
        <span>已有账号? </span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script>
import { User, Lock, Message } from '@element-plus/icons-vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

export default {
  name: 'Register',
  components: {
    User,
    Lock,
    Message
  },
  data() {
    return {
      registerForm: {
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: this.validatePassword, trigger: 'blur' }
        ]
      },
      loading: false
    };
  },
  methods: {
    validatePassword(rule, value, callback) {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    },

    async handleRegister() {
      this.loading = true;
      try {
        await this.$refs.registerForm.validate();
        const response = await axios.post('/api/auth/register', {
          username: this.registerForm.username,
          email: this.registerForm.email,
          password: this.registerForm.password
        });

        // 兼容后端返回：有token则自动登录，无token则提示手动登录
        if (response.data.token) {
          localStorage.setItem('token', response.data.token);
          localStorage.setItem('username', response.data.username);
          this.$router.push('/dashboard');
          ElMessage.success('注册成功，已自动登录');
        } else {
          ElMessage.success(response.data.message || '注册成功，请手动登录');
          this.$router.push('/login');
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '注册失败，请稍后重试');
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped lang="scss">
/* 样式与Login.vue类似，可复用 */
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
  max-width: 450px;
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

.register-form {
  padding: 2rem;
}

.register-button {
  width: 100%;
  padding: 0.8rem;
  font-size: 1rem;
}

.login-link {
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