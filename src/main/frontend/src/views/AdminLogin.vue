<template>
  <div class="admin-login">
    <div class="login-container">
      <div class="login-header">
        <h2>管理员登录</h2>
        <p>智能媒体平台管理后台</p>
      </div>
      
      <div class="login-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            type="text" 
            id="username" 
            v-model="loginForm.username" 
            placeholder="请输入管理员用户名"
            @keyup.enter="handleLogin"
          />
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            type="password" 
            id="password" 
            v-model="loginForm.password" 
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
          />
        </div>
        
        <button 
          class="login-btn" 
          @click="handleLogin"
          :disabled="loading"
        >
          {{ loading ? '登录中...' : '登录' }}
        </button>
        
        <div class="error-message" v-if="errorMessage">
          {{ errorMessage }}
        </div>
        
        <!-- 调试信息区域 -->
        <div class="debug-info" style="margin-top: 20px; padding: 10px; background: #f5f5f5; border-radius: 6px; font-size: 12px;">
          <h4>调试信息：</h4>
          <p>当前登录状态: {{ isAdmin ? '已登录' : '未登录' }}</p>
          <p>管理员信息: {{ adminInfo || '无' }}</p>
          <div style="margin-top: 10px;">
            <button @click="clearStorage" style="margin-right: 10px; padding: 5px 10px; background: #ff4757; color: white; border: none; border-radius: 4px; cursor: pointer;">
              清除登录状态
            </button>
            <button @click="checkAdminStatus" style="margin-right: 10px; padding: 5px 10px; background: #2ed573; color: white; border: none; border-radius: 4px; cursor: pointer;">
              检查管理员状态
            </button>
            <button @click="createDefaultAdmin" style="padding: 5px 10px; background: #3742fa; color: white; border: none; border-radius: 4px; cursor: pointer;">
              创建默认管理员
            </button>
          </div>
        </div>
      </div>
      
      <div class="login-footer">
        <p>© 2024 智能媒体平台 - 管理后台</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import request from '../utils/request'

export default {
  name: 'AdminLogin',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const errorMessage = ref('')
    
    // 调试信息
    const isAdmin = ref(localStorage.getItem('isAdmin') === 'true')
    const adminInfo = ref(localStorage.getItem('adminInfo'))
    
    const loginForm = reactive({
      username: '',
      password: ''
    })
    
    const clearStorage = () => {
      localStorage.removeItem('adminInfo')
      localStorage.removeItem('isAdmin')
      isAdmin.value = false
      adminInfo.value = null
      ElMessage.success('已清除登录状态')
    }
    
    const checkAdminStatus = async () => {
      try {
        const response = await adminRequest.get('/admin/test/status')
        console.log('管理员状态:', response.data)
        alert(`管理员状态: ${response.data.message}\n管理员数量: ${response.data.adminCount}`)
      } catch (error) {
        console.error('检查管理员状态失败:', error)
        ElMessage.error('检查管理员状态失败')
      }
    }
    
    const createDefaultAdmin = async () => {
      try {
        const response = await adminRequest.post('/admin/test/create-default')
        console.log('创建默认管理员:', response.data)
        ElMessage.success('默认管理员创建成功！用户名: admin, 密码: admin123')
      } catch (error) {
        console.error('创建默认管理员失败:', error)
        ElMessage.error(error.response?.data?.message || '创建默认管理员失败')
      }
    }
    
    const handleLogin = async () => {
      if (!loginForm.username || !loginForm.password) {
        errorMessage.value = '请输入用户名和密码'
        return
      }
      
      loading.value = true
      errorMessage.value = ''
      
      try {
        // 管理员登录不使用普通用户的token
        const adminRequest = axios.create({
          baseURL: '/api',
          timeout: 5000,
          withCredentials: false
        });
        
        const response = await adminRequest.post('/admin/login', loginForm)
        console.log('登录响应:', response) // 调试日志
        
        if (response.data) {
          // 保存管理员信息到localStorage
          localStorage.setItem('adminInfo', JSON.stringify(response.data))
          localStorage.setItem('isAdmin', 'true')
          
          // 更新调试信息
          isAdmin.value = true
          adminInfo.value = JSON.stringify(response.data)
          
          // 显示成功提示
          ElMessage.success('登录成功！正在跳转到管理后台...')
          
          // 延迟跳转，让用户看到成功提示
          setTimeout(() => {
            router.push('/admin/dashboard')
          }, 1000)
        }
      } catch (error) {
        console.error('登录失败:', error)
        const errorMsg = error.response?.data?.message || error.message || '登录失败，请检查用户名和密码'
        errorMessage.value = errorMsg
        ElMessage.error(errorMsg)
      } finally {
        loading.value = false
      }
    }
    
    return {
      loginForm,
      loading,
      errorMessage,
      isAdmin,
      adminInfo,
      clearStorage,
      checkAdminStatus,
      createDefaultAdmin,
      handleLogin
    }
  }
}
</script>

<style scoped>
.admin-login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #333;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.login-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error-message {
  margin-top: 16px;
  padding: 12px;
  background: #fee;
  border: 1px solid #fcc;
  border-radius: 6px;
  color: #c33;
  font-size: 14px;
  text-align: center;
}

.login-footer {
  text-align: center;
  color: #999;
  font-size: 12px;
}
</style> 