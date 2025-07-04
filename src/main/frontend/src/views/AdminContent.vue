<template>
  <div class="admin-content">
    <!-- 搜索和筛选 -->
    <div class="filters">
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索内容标题..."
          @keyup.enter="handleSearch"
        />
        <button @click="handleSearch">搜索</button>
      </div>
      
      <div class="filter-options">
        <select v-model="statusFilter" @change="handleFilter">
          <option value="">全部状态</option>
          <option value="published">已发布</option>
          <option value="pending">待审核</option>
        </select>
      </div>
    </div>
    
    <!-- 内容列表 -->
    <div class="content-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>作者</th>
            <th>状态</th>
            <th>浏览量</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="content in contentList" :key="content.id">
            <td>{{ content.id }}</td>
            <td class="title-cell">
              <div class="content-title" @click="viewContent(content)">
                {{ content.title }}
              </div>
            </td>
            <td>用户{{ content.createdBy }}</td>
            <td>
              <span class="status-badge" :class="content.isPublished ? 'published' : 'pending'">
                {{ content.isPublished ? '已发布' : '待审核' }}
              </span>
            </td>
            <td>{{ content.viewCount }}</td>
            <td>{{ formatDate(content.createdAt) }}</td>
            <td class="actions">
              <button 
                v-if="!content.isPublished" 
                class="btn-approve" 
                @click="approveContent(content.id)"
              >
                审核通过
              </button>
              <button 
                v-if="content.isPublished" 
                class="btn-reject" 
                @click="rejectContent(content.id)"
              >
                取消发布
              </button>
              <button class="btn-view" @click="viewContent(content)">
                查看
              </button>
              <button class="btn-delete" @click="deleteContent(content.id)">
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
    
    <!-- 内容详情模态框 -->
    <div v-if="showContentModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ selectedContent?.title }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div class="content-info">
            <p><strong>作者:</strong> 用户{{ selectedContent?.createdBy }}</p>
            <p><strong>状态:</strong> 
              <span class="status-badge" :class="selectedContent?.isPublished ? 'published' : 'pending'">
                {{ selectedContent?.isPublished ? '已发布' : '待审核' }}
              </span>
            </p>
            <p><strong>创建时间:</strong> {{ formatDate(selectedContent?.createdAt) }}</p>
            <p><strong>浏览量:</strong> {{ selectedContent?.viewCount }}</p>
          </div>
          <div class="content-preview">
            <h4>内容预览:</h4>
            <div class="content-text" v-html="selectedContent?.content"></div>
          </div>
        </div>
        <div class="modal-footer">
          <button 
            v-if="!selectedContent?.isPublished" 
            class="btn-approve" 
            @click="approveContent(selectedContent?.id)"
          >
            审核通过
          </button>
          <button 
            v-if="selectedContent?.isPublished" 
            class="btn-reject" 
            @click="rejectContent(selectedContent?.id)"
          >
            取消发布
          </button>
          <button class="btn-delete" @click="deleteContent(selectedContent?.id)">
            删除
          </button>
          <button class="btn-cancel" @click="closeModal">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

export default {
  name: 'AdminContent',
  setup() {
    const contentList = ref([])
    const currentPage = ref(0)
    const totalPages = ref(0)
    const searchKeyword = ref('')
    const statusFilter = ref('')
    const showContentModal = ref(false)
    const selectedContent = ref(null)
    const loading = ref(false)
    
    const fetchContentList = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value,
          size: 10
        }
        
        if (searchKeyword.value) {
          params.keyword = searchKeyword.value
        }
        
        if (statusFilter.value) {
          params.status = statusFilter.value
        }
        
        const response = await request.get('/admin/content/list', { params })
        contentList.value = response.data.content
        totalPages.value = response.data.totalPages
      } catch (error) {
        console.error('获取内容列表失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    const handleSearch = () => {
      currentPage.value = 0
      fetchContentList()
    }
    
    const handleFilter = () => {
      currentPage.value = 0
      fetchContentList()
    }
    
    const changePage = (page) => {
      currentPage.value = page
      fetchContentList()
    }
    
    const approveContent = async (id) => {
      try {
        await request.put(`/admin/content/${id}/approve`)
        alert('内容审核通过')
        fetchContentList()
        closeModal()
      } catch (error) {
        alert('操作失败: ' + error.response?.data?.message)
      }
    }
    
    const rejectContent = async (id) => {
      try {
        await request.put(`/admin/content/${id}/reject`)
        alert('内容已取消发布')
        fetchContentList()
        closeModal()
      } catch (error) {
        alert('操作失败: ' + error.response?.data?.message)
      }
    }
    
    const deleteContent = async (id) => {
      if (!confirm('确定要删除这个内容吗？')) return
      
      try {
        await request.delete(`/admin/content/${id}`)
        alert('内容删除成功')
        fetchContentList()
        closeModal()
      } catch (error) {
        alert('删除失败: ' + error.response?.data?.message)
      }
    }
    
    const viewContent = (content) => {
      selectedContent.value = content
      showContentModal.value = true
    }
    
    const closeModal = () => {
      showContentModal.value = false
      selectedContent.value = null
    }
    
    const formatDate = (dateString) => {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString('zh-CN')
    }
    
    onMounted(() => {
      fetchContentList()
    })
    
    return {
      contentList,
      currentPage,
      totalPages,
      searchKeyword,
      statusFilter,
      showContentModal,
      selectedContent,
      loading,
      handleSearch,
      handleFilter,
      changePage,
      approveContent,
      rejectContent,
      deleteContent,
      viewContent,
      closeModal,
      formatDate
    }
  }
}
</script>

<style scoped>
.admin-content {
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

.filter-options select {
  padding: 10px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  background: white;
}

.content-table {
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

.title-cell {
  max-width: 300px;
}

.content-title {
  cursor: pointer;
  color: #667eea;
  text-decoration: underline;
}

.content-title:hover {
  color: #5a6fd8;
}

.status-badge {
  padding: 4px 8px;
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

.btn-approve {
  background: #4caf50;
  color: white;
}

.btn-reject {
  background: #ff9800;
  color: white;
}

.btn-view {
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
  max-width: 800px;
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

.content-info {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.content-info p {
  margin: 8px 0;
  color: #333;
}

.content-preview h4 {
  margin: 0 0 12px 0;
  color: #333;
}

.content-text {
  padding: 16px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  background: #fafafa;
  max-height: 300px;
  overflow-y: auto;
  line-height: 1.6;
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

.btn-cancel {
  background: #6c757d;
  color: white;
}

@media (max-width: 768px) {
  .filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .content-table {
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