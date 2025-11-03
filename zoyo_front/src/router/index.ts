import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'system/user',
        name: 'UserManagement',
        component: () => import('@/views/system/UserManagement.vue'),
        meta: { 
          requiresAuth: true
          // permission: 'system:user' // 暂时移除权限检查
        }
      },
      {
        path: 'system/department',
        name: 'DepartmentManagement',
        component: () => import('@/views/system/DepartmentManagement.vue'),
        meta: { 
          requiresAuth: true
          // permission: 'system:dept' // 暂时移除权限检查
        }
      },
      {
        path: 'system/role',
        name: 'RoleManagement',
        component: () => import('@/views/system/RoleManagement.vue'),
        meta: { 
          requiresAuth: true
          // permission: 'system:role' // 暂时移除权限检查
        }
      },
      {
        path: 'system/permission',
        name: 'PermissionManagement',
        component: () => import('@/views/system/PermissionManagement.vue'),
        meta: { 
          requiresAuth: true
          // permission: 'system:permission' // 暂时移除权限检查
        }
      },
      {
        path: 'system/login-log',
        name: 'LoginLogManagement',
        component: () => import('@/views/system/LoginLogManagement.vue'),
        meta: { 
          requiresAuth: true
          // permission: 'system:loginlog' // 暂时移除权限检查
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !userStore.token) {
    // 需要登录但未登录，跳转到登录页
    next('/login')
  } else if (!requiresAuth && userStore.token && (to.path === '/login' || to.path === '/register')) {
    // 已登录但访问登录/注册页，跳转到监控大屏
    next('/dashboard')
  } else {
    next()
  }
})

export default router
