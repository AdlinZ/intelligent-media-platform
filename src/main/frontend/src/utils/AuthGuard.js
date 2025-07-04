import { ElMessage } from 'element-plus';

export default function(to, from, next) {
  // 检查localStorage中是否有token
  const token = localStorage.getItem('token');
  
  if (!token) {
    ElMessage.warning('请先登录');
    next('/login');
    return;
  }
  
  next();
}