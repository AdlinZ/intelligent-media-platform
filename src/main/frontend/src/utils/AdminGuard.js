export default function AdminGuard(to, from, next) {
  const isAdmin = localStorage.getItem('isAdmin')
  const adminInfo = localStorage.getItem('adminInfo')
  
  if (to.path === '/admin/login') {
    // 如果已经登录，重定向到管理后台
    if (isAdmin && adminInfo) {
      next('/admin/dashboard')
    } else {
      next()
    }
  } else {
    // 检查是否已登录
    if (!isAdmin || !adminInfo) {
      next('/admin/login')
    } else {
      next()
    }
  }
} 