import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '../router';

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 5000,
  withCredentials: false // 禁用跨域凭证，避免与token认证冲突
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    console.log('当前token:', token); // 添加此行调试
    if (token) {
      config.headers.Authorization = `Bearer ${token}`; // 必须包含Bearer前缀
      console.log('已添加Authorization头:', config.headers.Authorization);
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    // 处理401未授权错误
    if (error.response && error.response.status === 401) {
      // 清除本地存储并跳转登录页
      localStorage.removeItem('token');
      localStorage.removeItem('tokenType');
      ElMessage.error('登录已过期，请重新登录');
      router.push('/login');
    } else if (error.response && error.response.status === 403) {
      ElMessage.error('您没有访问该资源的权限');
    } else {
      ElMessage.error(error.response?.data?.message || '请求失败');
    }
    return Promise.reject(error);
  }
);

export default service;