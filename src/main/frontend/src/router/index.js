import { createRouter, createWebHashHistory, createWebHistory } from 'vue-router';
import Introduction from '../views/Introduction.vue';
import TopicSelection from '../views/TopicSelection.vue';
import ContentEditor from '../views/ContentEditor.vue';
import Dashboard from '../views/Dashboard.vue';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
// 注释或删除这行：import ForgotPassword from '../views/ForgotPassword.vue';
import AuthGuard from '../utils/AuthGuard';
import AdminGuard from '../utils/AdminGuard';
import Features from '../views/Features.vue';
import About from '../views/About.vue';
import Tutorial from '../views/Tutorial.vue';
import Profile from '../views/Profile.vue';
import ArticleList from '../views/ArticleList.vue';
import ArticleDetail from '../views/ArticleDetail.vue';

// 管理端页面
import AdminLogin from '../views/AdminLogin.vue';
import AdminLayout from '../views/AdminLayout.vue';
import AdminDashboard from '../views/AdminDashboard.vue';
import AdminContent from '../views/AdminContent.vue';
import AdminUsers from '../views/AdminUsers.vue';
import AdminAdmins from '../views/AdminAdmins.vue';
import TestPage from '../views/TestPage.vue';

const routes = [
  { path: '/', name: 'Introduction', component: Introduction },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  // 注释或删除这行：{ path: '/forgot-password', name: 'ForgotPassword', component: ForgotPassword },
  { path: '/topics', name: 'TopicSelection', component: TopicSelection, beforeEnter: AuthGuard },
  { path: '/editor/:topicId', name: 'ContentEditor', component: ContentEditor, beforeEnter: AuthGuard },
  { path: '/dashboard', name: 'Dashboard', component: Dashboard, beforeEnter: AuthGuard },
  { path: '/features', name: 'Features', component: Features, meta: { requiresAuth: false } },
  { path: '/tutorial', name: 'Tutorial', component: Tutorial, meta: { requiresAuth: false } },
  { path: '/profile', name: 'Profile', component: Profile, beforeEnter: AuthGuard },
  { path: '/about', name: 'About', component: About, meta: { requiresAuth: false } },
  { path: '/articles', name: 'ArticleList', component: ArticleList, meta: { requiresAuth: true }},
  { path: '/articles/:id', name: 'ArticleDetail', component: ArticleDetail, meta: { requiresAuth: true }},
  { path: '/test', name: 'TestPage', component: TestPage },
  
  // 管理端路由
  { path: '/admin/login', name: 'AdminLogin', component: AdminLogin },
  { 
    path: '/admin', 
    component: AdminLayout,
    beforeEnter: AdminGuard,
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: AdminDashboard },
      { path: 'content', name: 'AdminContent', component: AdminContent },
      { path: 'users', name: 'AdminUsers', component: AdminUsers },
      { path: 'admins', name: 'AdminAdmins', component: AdminAdmins },
      { path: '', redirect: '/admin/dashboard' }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes
});

export default router;