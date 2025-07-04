<template>
  <div class="admin-admins">
    <!-- 操作按钮 -->
    <div class="actions-header">
      <button class="btn-add" @click="showAddModal = true">
        + 添加管理员
      </button>
    </div>
    
    <!-- 管理员列表 -->
    <div class="admins-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>真实姓名</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>状态</th>
            <th>最后登录</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="admin in adminList" :key="admin.id">
            <td>{{ admin.id }}</td>
            <td>{{ admin.username }}</td>
            <td>{{ admin.realName || '-' }}</td>
            <td>{{ admin.email }}</td>
            <td>
              <span class="role-badge" :class="admin.role.toLowerCase()">
                {{ getRoleName(admin.role) }}
              </span>
            </td>
            <td>
              <span class="status-badge" :class="admin.isActive ? 'active' : 'inactive'">
                {{ admin.isActive ? '启用' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(admin.lastLogin) }}</td>
            <td>{{ formatDate(admin.createdAt) }}</td>
            <td class="actions">
              <button class="btn-edit" @click="editAdmin(admin)">
                编辑
              </button>
              <button 
                v-if="admin.id !== currentAdminId"
                class="btn-delete" 
                @click="deleteAdmin(admin.id)"
              >
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
    
    <!-- 添加/编辑管理员模态框 -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑管理员' : '添加管理员' }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>用户名</label>
            <input 
              type="text" 
              v-model="adminForm.username" 
              :disabled="showEditModal"
              placeholder="请输入用户名"
            />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input 
              type="email" 
              v-model="adminForm.email" 
              placeholder="请输入邮箱"
            />
          </div>
          <div class="form-group">
            <label>真实姓名</label>
            <input 
              type="text" 
              v-model="adminForm.realName" 
              placeholder="请输入真实姓名"
            />
          </div>
          <div class="form-group">
            <label>手机</label>
            <input 
              type="text" 
              v-model="adminForm.phone" 
              placeholder="请输入手机号"
            />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input 
              type="password" 
              v-model="adminForm.password" 
              :placeholder="showEditModal ? '留空则不修改' : '请输入密码'"
            />
          </div>
          <div class="form-group">
            <label>角色</label>
            <select v-model="adminForm.role">
              <option value="MODERATOR">审核员</option>
              <option value="ADMIN">管理员</option>
              <option value="SUPER_ADMIN">超级管理员</option>
            </select>
          </div>
          <div class="form-group">
            <label>状态</label>
            <select v-model="adminForm.isActive">
              <option :value="true">启用</option>
              <option :value="false">禁用</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-save" @click="saveAdmin">
            {{ showEditModal ? '保存' : '添加' }}
          </button>
          <button class="btn-cancel" @click="closeModal">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import request from '../utils/request'

export default {
  name: 'AdminAdmins',
  setup() {
    const adminList = ref([])
    const currentPage = ref(0)
    const totalPages = ref(0)
    const showAddModal = ref(false)
    const showEditModal = ref(false)
    const selectedAdmin = ref(null)
    const currentAdminId = ref(null)
    const loading = ref(false)
    
    const adminForm = reactive({
      username: '',
      email: '',
      realName: '',
      phone: '',
      password: '',
      role: 'ADMIN',
      isActive: true
    })
    
    const fetchAdminList = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value,
          size: 10
        }
        
        const response = await request.get('/admin/admins', { params })
        adminList.value = response.data.content
        totalPages.value = response.data.totalPages
      } catch (error) {
        console.error('获取管理员列表失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const changePage = (page) => {
      currentPage.value = page
      fetchAdminList()
    }
    
    const editAdmin = (admin) => {
      selectedAdmin.value = admin
      adminForm.username = admin.username
      adminForm.email = admin.email
      adminForm.realName = admin.realName || ''
      adminForm.phone = admin.phone || ''
      adminForm.password = ''
      adminForm.role = admin.role
      adminForm.isActive = admin.isActive
      showEditModal.value = true
    }
    
    const saveAdmin = async () => {
      try {
        if (showEditModal.value) {
          // 编辑管理员
          const updateData = { ...adminForm }
          if (!updateData.password) {
            delete updateData.password
          }
          await request.put(`/admin/admins/${selectedAdmin.value.id}`, updateData)
          alert('管理员信息更新成功')
        } else {
          // 添加管理员
          await request.post('/admin/admins', adminForm)
          alert('管理员添加成功')
        }
        closeModal()
        fetchAdminList()
      } catch (error) {
        alert('操作失败: ' + error.response?.data?.message)
      }
    }
    
    const deleteAdmin = async (id) => {
      if (!confirm('确定要删除这个管理员吗？')) return
      
      try {
        await request.delete(`/admin/admins/${id}`)
        alert('管理员删除成功')
        fetchAdminList()
      } catch (error) {
        alert('删除失败: ' + error.response?.data?.message)
      }
    }
    
    const closeModal = () => {
      showAddModal.value = false
      showEditModal.value = false
      selectedAdmin.value = null
      Object.assign(adminForm, {
        username: '',
        email: '',
        realName: '',
        phone: '',
        password: '',
        role: 'ADMIN',
        isActive: true
      })
    }
    
    const getRoleName = (role) => {
      const roleNames = {
        'SUPER_ADMIN': '超级管理员',
        'ADMIN': '管理员',
        'MODERATOR': '审核员'
      }
      return roleNames[role] || role
    }
    
    const formatDate = (dateString) => {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleString('zh-CN')
    }
    
    onMounted(() => {
      // 获取当前管理员ID
      const adminInfo = localStorage.getItem('adminInfo')
      if (adminInfo) {
        const admin = JSON.parse(adminInfo)
        currentAdminId.value = admin.id
      }
      fetchAdminList()
    })
    
    return {
      adminList,
      currentPage,
      totalPages,
      showAddModal,
      showEditModal,
      selectedAdmin,
      currentAdminId,
      adminForm,
      loading,
      changePage,
      editAdmin,
      saveAdmin,
      deleteAdmin,
      closeModal,
      getRoleName,
      formatDate
    }
  }
}
</script>

<style scoped>
.admin-admins {
  max-width: 1200px;
  margin: 0 auto;
}

.actions-header {
  margin-bottom: 20px;
}

.btn-add {
  padding: 12px 24px;
  background: #4caf50;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.admins-table {
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

.role-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.role-badge.super_admin {
  background: #ffebee;
  color: #c62828;
}

.role-badge.admin {
  background: #e3f2fd;
  color: #1565c0;
}

.role-badge.moderator {
  background: #f3e5f5;
  color: #7b1fa2;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background: #e8f5e8;
  color: #2e7d32;
}

.status-badge.inactive {
  background: #ffebee;
  color: #c62828;
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

@media (max-width: 768px) {
  .admins-table {
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