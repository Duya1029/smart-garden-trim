import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore, ROLE_HOME } from '@/store/user'
import { getToken } from '@/utils/auth'
import Layout from '@/layout/index.vue'

// 常量路由（不需要权限 / 不在菜单中展示）
export const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true, title: '登录' }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: { hidden: true, title: '无权限' }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { hidden: true, title: '页面不存在' }
  },
  {
    // 根路径占位，具体跳转由全局守卫按角色决定
    path: '/',
    component: Layout,
    redirect: '/login',
    meta: { hidden: true }
  }
]

// 业务路由（同时作为侧边菜单数据源，按 meta.roles 过滤）
export const asyncRoutes = [
  {
    path: '/admin',
    component: Layout,
    redirect: '/admin/dashboard',
    meta: { title: '管理后台', icon: 'Setting', roles: ['ADMIN'] },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/dashboard/index.vue'),
        meta: { title: '数据看板', icon: 'DataLine', roles: ['ADMIN'] }
      },
      {
        path: 'user',
        name: 'AdminUser',
        component: () => import('@/views/admin/user/index.vue'),
        meta: { title: '账号管理', icon: 'User', roles: ['ADMIN'] }
      },
      {
        path: 'dept',
        name: 'AdminDept',
        component: () => import('@/views/admin/dept/index.vue'),
        meta: { title: '组织架构', icon: 'OfficeBuilding', roles: ['ADMIN'] }
      },
      {
        path: 'client',
        name: 'AdminClient',
        component: () => import('@/views/admin/client/index.vue'),
        meta: { title: '客户主体', icon: 'Avatar', roles: ['ADMIN'] }
      },
      {
        path: 'tree',
        name: 'AdminTree',
        component: () => import('@/views/admin/tree/index.vue'),
        meta: { title: '树木档案', icon: 'Location', roles: ['ADMIN'] }
      },
      {
        path: 'trimRequest',
        name: 'AdminTrimRequest',
        component: () => import('@/views/admin/trimRequest/index.vue'),
        meta: { title: '修剪需求审核', icon: 'DocumentChecked', roles: ['ADMIN'] }
      },
      {
        path: 'trimTask',
        name: 'AdminTrimTask',
        component: () => import('@/views/admin/trimTask/index.vue'),
        meta: { title: '任务管理', icon: 'Tickets', roles: ['ADMIN'] }
      },
      {
        path: 'trimRecord',
        name: 'AdminTrimRecord',
        component: () => import('@/views/admin/trimRecord/index.vue'),
        meta: { title: '修剪记录', icon: 'List', roles: ['ADMIN'] }
      }
    ]
  },
  {
    path: '/worker',
    component: Layout,
    redirect: '/worker/task',
    meta: { title: '养护工作台', icon: 'Tools', roles: ['WORKER'] },
    children: [
      {
        path: 'task',
        name: 'WorkerTask',
        component: () => import('@/views/worker/task/index.vue'),
        meta: { title: '我的任务', icon: 'Notebook', roles: ['WORKER'] }
      },
      {
        path: 'task/:id',
        name: 'WorkerTaskDetail',
        component: () => import('@/views/worker/task/detail.vue'),
        // activeMenu 指定在侧边菜单中高亮的菜单项
        meta: { title: '任务详情', hidden: true, activeMenu: '/worker/task', roles: ['WORKER'] }
      }
    ]
  },
  {
    path: '/client',
    component: Layout,
    redirect: '/client/tree',
    meta: { title: '客户服务台', icon: 'Avatar', roles: ['CLIENT'] },
    children: [
      {
        path: 'tree',
        name: 'ClientTree',
        component: () => import('@/views/client/tree/index.vue'),
        meta: { title: '我的树木', icon: 'Location', roles: ['CLIENT'] }
      },
      {
        path: 'request',
        name: 'ClientRequest',
        component: () => import('@/views/client/request/index.vue'),
        meta: { title: '修剪需求', icon: 'EditPen', roles: ['CLIENT'] }
      },
      {
        path: 'report',
        name: 'ClientReport',
        component: () => import('@/views/client/report/index.vue'),
        meta: { title: '修剪报告', icon: 'Document', roles: ['CLIENT'] }
      },
      {
        path: 'score',
        name: 'ClientScore',
        component: () => import('@/views/client/score/index.vue'),
        meta: { title: '评分趋势', icon: 'TrendCharts', roles: ['CLIENT'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...constantRoutes, ...asyncRoutes, { path: '/:pathMatch(.*)*', redirect: '/404' }],
  scrollBehavior: () => ({ top: 0 })
})

// 白名单：无需登录即可访问
const WHITE_LIST = ['/login', '/403', '/404']

// 全局前置守卫：登录校验 + 角色权限校验
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const token = getToken()

  // 已登录用户访问登录页 → 直接回到角色首页
  if (to.path === '/login') {
    if (token && userStore.userInfo) {
      return next(userStore.homePath)
    }
    return next()
  }

  // 白名单放行
  if (WHITE_LIST.includes(to.path)) {
    return next()
  }

  // 未登录 → 跳转登录页
  if (!token) {
    return next('/login')
  }

  // 有 token 但无用户信息（如手动清缓存）→ 主动拉取
  if (!userStore.userInfo) {
    try {
      await userStore.fetchInfo()
    } catch (e) {
      userStore.reset()
      return next('/login')
    }
  }

  // 根路径 → 按角色跳转对应首页
  if (to.path === '/') {
    return next(userStore.homePath)
  }

  // 角色权限校验：route.meta.roles 声明可访问角色
  const roles = to.meta?.roles
  if (roles && roles.length && !roles.includes(userStore.role)) {
    return next('/403')
  }

  next()
})

// 后置钩子：设置页面标题
router.afterEach((to) => {
  const title = to.meta?.title
  document.title = title ? `${title} - 智慧园林·树木智能修剪决策平台` : '智慧园林·树木智能修剪决策平台'
})

export default router
