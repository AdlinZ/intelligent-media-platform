<template>
  <div class="admin-users">
    <!-- 搜索和筛选 -->
    <div class="filters">
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索用户名或邮箱..."
          @keyup.enter="handleSearch"
        />
        <button @click="handleSearch">搜索</button>
      </div>
    </div>
    
    <!-- 用户列表 -->
    <div class="users-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>手机</th>
            <th>注册时间</th>
            <th>内容数量</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in userList" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.phone || '-' }}</td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td>{{ user.contentCount || 0 }}</td>
            <td class="actions">
              <button class="btn-edit" @click="editUser(user)">
                编辑
              </button>
              <button class="btn-view" @click="viewUserContents(user.id)">
                查看内容
              </button>
              <button class="btn-delete" @click="deleteUser(user.id)">
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 分页 -->
    <div class="pagination">
      <button 
        :disabled="currentPage === 0" 
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <span>第 {{ currentPage + 1 }} 页，共 {{ totalPages }} 页</span>
      <button 
        :disabled="currentPage >= totalPages - 1" 
        @click="changePage(currentPage + 1)"
      >
        下一页
      </button>
    </div>
    
    <!-- 编辑用户模态框 -->
    <div v-if="showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>编辑用户</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>用户名</label>
            <input type="text" v-model="editForm.username" disabled />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input type="email" v-model="editForm.email" />
          </div>
          <div class="form-group">
            <label>手机</label>
            <input type="text" v-model="editForm.phone" />
          </div>
          <div class="form-group">
            <label>新密码 (留空则不修改)</label>
            <input type="password" v-model="editForm.password" placeholder="输入新密码" />
          </div>
          <div class="form-group">
            <label>角色</label>
            <select v-model="editForm.roles">
              <option value="USER">普通用户</option>
              <option value="ADMIN">管理员</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-save" @click="saveUser">保存</button>
          <button class="btn-cancel" @click="closeModal">取消</button>
        </div>
      </div>
    </div>
    
    <!-- 用户内容模态框 -->
    <div v-if="showContentsModal" class="modal-overlay" @click="closeContentsModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>用户内容列表</h3>
          <button class="close-btn" @click="closeContentsModal">×</button>
        </div>
        <div class="modal-body">
          <div v-if="userContents.length === 0" class="no-content">
            该用户暂无内容
          </div>
          <div v-else class="contents-list">
            <div 
              v-for="content in userContents" 
              :key="content.id"
              class="content-item"
            >
              <div class="content-info">
                <h4>{{ content.title }}</h4>
                <p>状态: 
                  <span class="status-badge" :class="content.isPublished ? 'published' : 'pending'">
                    {{ content.isPublished ? '已发布' : '待审核' }}
                  </span>
                </p>
                <p>创建时间: {{ formatDate(content.createdAt) }}</p>
                <p>浏览量: {{ content.viewCount }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeContentsModal">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import request from '../utils/request'

export default {
  name: 'AdminUsers',
  setup() {
    const userList = ref([])
    const currentPage = ref(0)
    const totalPages = ref(0)
    const searchKeyword = ref('')
    const showEditModal = ref(false)
    const showContentsModal = ref(false)
    const selectedUser = ref(null)
    const userContents = ref([])
    const loading = ref(false)
    
    const editForm = reactive({
      username: '',
      email: '',
      phone: '',
      password: '',
      roles: 'USER'
    })
    
    const fetchUserList = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value,
          size: 10
        }
        
        if (searchKeyword.value) {
          params.keyword = searchKeyword.value
        }
        
        const response = await request.get('/admin/users/list', { params })
        userList.value = response.data.content
        totalPages.value = response.data.totalPages
      } catch (error) {
        console.error('获取用户列表失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const handleSearch = () => {
      currentPage.value = 0
      fetchUserList()
    }
    
    const changePage = (page) => {
      currentPage.value = page
      fetchUserList()
    }
    
    const editUser = (user) => {
      selectedUser.value = user
      editForm.username = user.username
      editForm.email = user.email
      editForm.phone = user.phone || ''
      editForm.password = ''
      editForm.roles = user.roles?.includes('ADMIN') ? 'ADMIN' : 'USER'
      showEditModal.value = true
    }
    
    const saveUser = async () => {
      try {
        const updateData = {
          email: editForm.email,
          phone: editForm.phone,
          roles: [editForm.roles]
        }
        
        if (editForm.password) {
          updateData.password = editForm.password
        }
        
        await request.put(`/admin/users/${selectedUser.value.id}`, updateData)
        alert('用户信息更新成功')
        closeModal()
        fetchUserList()
      } catch (error) {
        alert('更新失败: ' + error.response?.data?.message)
      }
    }
    
    const deleteUser = async (id) => {
      if (!confirm('确定要删除这个用户吗？这将同时删除该用户的所有内容！')) return
      
      try {
        await request.delete(`/admin/users/${id}`)
        alert('用户删除成功')
        fetchUserList()
      } catch (error) {
        alert('删除失败: ' + error.response?.data?.message)
      }
    }
    
    const viewUserContents = async (userId) => {
      try {
        const response = await request.get(`/admin/users/${userId}/contents`)
        userContents.value = response.data || []
        showContentsModal.value = true
      } catch (error) {
        alert('获取用户内容失败: ' + error.response?.data?.message)
      }
    }
    
    const closeModal = () => {
      showEditModal.value = false
      selectedUser.value = null
      Object.assign(editForm, {
        username: '',
        email: '',
        phone: '',
        password: '',
        roles: 'USER'
      })
    }
    
    const closeContentsModal = () => {
      showContentsModal.value = false
      userContents.value = []
    }
    
    const formatDate = (dateString) => {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString('zh-CN')
    }
    
    onMounted(() => {
      fetchUserList()
    })
    
    return {
      userList,
      currentPage,
      totalPages,
      searchKeyword,
      showEditModal,
      showContentsModal,
      selectedUser,
      userContents,
      editForm,
      loading,
      handleSearch,
      changePage,
      editUser,
      saveUser,
      deleteUser,
      viewUserContents,
      closeModal,
      closeContentsModal,
      formatDate
    }
  }
}
</script>

<style scoped>
.admin-users {
  max-width: 1200px;
  margin: 0 auto;
}

.filters {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  align-items: center;
}

.search-box {
  display: flex;
  gap: 10px;
  flex: 1;
}

.search-box input {
  flex: 1;
  padding: 10px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
}

.search-box button {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.users-table {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 16px;
  text-align: left;
  border-bottom: 1px solid #e1e5e9;
}

th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.actions button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
}

.btn-edit {
  background: #2196f3;
  color: white;
}

.btn-view {
  background: #4caf50;
  color: white;
}

.btn-delete {
  background: #f44336;
  color: white;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #e1e5e9;
  background: white;
  border-radius: 6px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination button:not(:disabled):hover {
  background: #f8f9fa;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #e1e5e9;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
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

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
}

.form-group input:disabled {
  background: #f8f9fa;
  color: #666;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #e1e5e9;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.modal-footer button {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.btn-save {
  background: #4caf50;
  color: white;
}

.btn-cancel {
  background: #6c757d;
  color: white;
}

.no-content {
  text-align: center;
  color: #666;
  padding: 40px;
}

.contents-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.content-item {
  padding: 16px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  background: #f8f9fa;
}

.content-info h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 16px;
}

.content-info p {
  margin: 4px 0;
  color: #666;
  font-size: 14px;
}

.status-badge {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.published {
  background: #e8f5e8;
  color: #2e7d32;
}

.status-badge.pending {
  background: #fff3e0;
  color: #f57c00;
}

@media (max-width: 768px) {
  .filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .users-table {
    overflow-x: auto;
  }
  
  .actions {
    flex-direction: column;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
}
</style> 