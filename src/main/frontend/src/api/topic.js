import request from '../utils/request';

// 选题生成
export const generateTopics = (data) => {
  return request.get('/topic/generate', {
    params: data
  });
};

// 获取热门选题
export const getHotTopics = () => {
  return request.get('/topic/hot');
};

// 内容生成
export const generateContent = (data) => {
  return request.post('/content/generate', data);
};

// 内容保存
export const saveContent = (data) => {
  return request.post('/content/save', data);
};

// Dashboard API
export const getDashboardStats = () => {
  return request.get('/api/dashboard/stats');
};

export const getTrafficData = (days = 7) => {
  return request.get('/api/dashboard/traffic', {
    params: { days }
  });
};

export const getContentAnalysis = () => {
  return request.get('/api/dashboard/content-analysis');
};

export const getUserEngagement = () => {
  return request.get('/api/dashboard/user-engagement');
};