<template>
  <div class="test-page">
    <h2>API测试页面</h2>
    
    <div class="test-section">
      <h3>管理员状态检查</h3>
      <button @click="checkAdmin">检查管理员状态</button>
      <div v-if="adminStatus" class="result">
        <pre>{{ JSON.stringify(adminStatus, null, 2) }}</pre>
      </div>
    </div>
    
    <div class="test-section">
      <h3>创建管理员</h3>
      <button @click="createAdmin">创建管理员</button>
      <div v-if="createResult" class="result">
        <pre>{{ JSON.stringify(createResult, null, 2) }}</pre>
      </div>
    </div>
    
    <div class="test-section">
      <h3>管理员登录测试</h3>
      <div class="login-form">
        <input v-model="loginForm.username" placeholder="用户名" />
        <input v-model="loginForm.password" type="password" placeholder="密码" />
        <button @click="testLogin">测试登录</button>
      </div>
      <div v-if="loginResult" class="result">
        <pre>{{ JSON.stringify(loginResult, null, 2) }}</pre>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import request from '../utils/request'

export default {
  name: 'TestPage',
  setup() {
    const adminStatus = ref(null)
    const createResult = ref(null)
    const loginResult = ref(null)
    
    const loginForm = reactive({
      username: 'admin',
      password: 'admin123'
    })
    
    const checkAdmin = async () => {
      try {
        const response = await request.get('/test/admin-check')
        adminStatus.value = response
      } catch (error) {
        adminStatus.value = { error: error.message }
      }
    }
    
    const createAdmin = async () => {
      try {
        const response = await request.post('/test/create-admin')
        createResult.value = response
      } catch (error) {
        createResult.value = { error: error.message }
      }
    }
    
    const testLogin = async () => {
      try {
        const response = await request.post('/admin/login', loginForm)
        loginResult.value = response
      } catch (error) {
        loginResult.value = { error: error.message }
      }
    }
    
    return {
      adminStatus,
      createResult,
      loginResult,
      loginForm,
      checkAdmin,
      createAdmin,
      testLogin
    }
  }
}
</script>

<style scoped>
.test-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.test-section h3 {
  margin-top: 0;
  color: #333;
}

.login-form {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.login-form input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background: #0056b3;
}

.result {
  margin-top: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
  overflow-x: auto;
}

.result pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style> 