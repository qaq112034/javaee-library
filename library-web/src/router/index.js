import { createRouter, createWebHashHistory } from 'vue-router'

/**
 * 路由配置
 *
 * 路由结构：
 *   /login          → 登录页（公开）
 *   /                → 主布局（需登录）
 *     /dashboard     → 仪表盘
 *     /book/list     → 图书列表
 *     /book/category → 分类管理
 *     /reader/list   → 读者列表
 *     /borrow/list   → 借阅记录
 *     /notice/list   → 公告列表
 *     /statistics    → 数据统计
 *     /system/user   → 用户管理
 *     /system/role   → 角色管理
 */

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'DataAnalysis' }
      },
      {
        path: 'book/list',
        name: 'BookList',
        component: () => import('@/views/book/BookList.vue'),
        meta: { title: '图书管理', icon: 'Notebook' }
      },
      {
        path: 'book/category',
        name: 'CategoryList',
        component: () => import('@/views/book/CategoryList.vue'),
        meta: { title: '分类管理', icon: 'Collection' }
      },
      {
        path: 'reader/list',
        name: 'ReaderList',
        component: () => import('@/views/reader/ReaderList.vue'),
        meta: { title: '读者管理', icon: 'User' }
      },
      {
        path: 'borrow/list',
        name: 'BorrowList',
        component: () => import('@/views/borrow/BorrowList.vue'),
        meta: { title: '借阅管理', icon: 'Tickets' }
      },
      {
        path: 'notice/list',
        name: 'NoticeList',
        component: () => import('@/views/notice/NoticeList.vue'),
        meta: { title: '公告管理', icon: 'Bell' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/Statistics.vue'),
        meta: { title: '数据统计', icon: 'DataAnalysis' }
      },
      {
        path: 'system/user',
        name: 'UserList',
        component: () => import('@/views/system/UserList.vue'),
        meta: { title: '用户管理', icon: 'Setting' }
      },
      {
        path: 'system/role',
        name: 'RoleList',
        component: () => import('@/views/system/RoleList.vue'),
        meta: { title: '角色管理', icon: 'Avatar' }
      }
    ]
  },
  {
    // 404 页面
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// ==================== 路由守卫 ====================
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 图书管理系统` : '图书管理系统'

  // 不需要认证的页面直接放行
  if (to.meta.noAuth) {
    return next()
  }

  // 检查是否已登录（有 Token）
  const token = localStorage.getItem('accessToken')
  if (!token) {
    return next('/login')
  }

  next()
})

export default router
