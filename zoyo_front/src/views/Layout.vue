<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <el-icon :size="28" color="#1e88e5"><Lock /></el-icon>
        <span v-show="!isCollapsed" class="logo-text">Zoyo Admin</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :unique-opened="true"
        router
      >
        <!-- 监控大屏 - 固定菜单 -->
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>监控大屏</template>
        </el-menu-item>

        <!-- 系统管理 - 动态菜单 -->
        <el-sub-menu v-if="systemMenus.length > 0" index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item 
            v-for="menu in systemMenus" 
            :key="menu.id" 
            :index="menu.routePath"
          >
            <el-icon>
              <component :is="getIconComponent(menu.icon)" />
            </el-icon>
            <template #title>{{ menu.permissionName }}</template>
          </el-menu-item>
        </el-sub-menu>

        <!-- 个人中心 - 固定菜单 -->
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <header class="navbar">
        <div class="navbar-left">
          <el-button text @click="toggleSidebar">
            <el-icon :size="20">
              <Expand v-if="isCollapsed" />
              <Fold v-else />
            </el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">监控大屏</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumb">{{ breadcrumb }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="navbar-right">
          <el-button text @click="refreshPage">
            <el-icon><Refresh /></el-icon>
          </el-button>
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-avatar">
              <el-avatar :size="36" style="background: linear-gradient(135deg, #2a5298 0%, #1e88e5 100%)">
                <el-icon :size="20"><UserFilled /></el-icon>
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { logout } from '@/api/auth'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)

// 获取系统管理菜单列表（根据用户权限动态显示）
const systemMenus = computed(() => {
  const menus = userStore.menus || []
  
  // 如果有菜单数据，从中查找系统管理菜单
  if (menus.length > 0) {
    // 查找系统管理菜单（兼容 'system' 和 'system:manage' 两种权限码）
    const systemMenu = menus.find(m => 
      m.permissionCode === 'system' || 
      m.permissionCode === 'system:manage' ||
      m.routePath === '/system'
    )
    
    if (systemMenu && systemMenu.children && systemMenu.children.length > 0) {
      return systemMenu.children
    }
    // 如果没有找到系统管理菜单或没有子菜单，返回空数组
    return []
  }
  
  // 如果没有菜单数据，返回空数组
  return []
})

// 获取图标组件
const getIconComponent = (iconName: string) => {
  if (!iconName) return 'Menu'
  // 将图标名称转换为组件
  const icons: Record<string, any> = ElementPlusIconsVue
  return icons[iconName] || 'Menu'
}

const breadcrumb = computed(() => {
  const map: Record<string, string> = {
    '/dashboard': '',
    '/system/user': '用户管理',
    '/system/department': '部门管理',
    '/system/role': '角色管理',
    '/system/permission': '权限管理',
    '/profile': '个人中心'
  }
  return map[route.path] || ''
})

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const refreshPage = () => {
  router.go(0)
}

const handleCommand = async (command: string) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '退出确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await logout()
        userStore.clearAuth()
        ElMessage.success('退出成功')
        router.push('/login')
      } catch (error) {
        userStore.clearAuth()
        router.push('/login')
      }
    }).catch(() => {
      // 取消操作
    })
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  background: #f0f2f5;
}

/* 侧边栏 */
.sidebar {
  width: 200px;
  background: #001529;
  transition: width 0.3s;
  overflow: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
}

.sidebar.collapsed {
  width: 64px;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: white;
  font-size: 20px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-text {
  white-space: nowrap;
}

:deep(.el-menu) {
  border-right: none;
  background: #001529;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.65);
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.1) !important;
}

:deep(.el-menu-item.is-active) {
  color: #fff;
  background: #1890ff !important;
}

:deep(.el-sub-menu .el-menu-item) {
  background: #000c17 !important;
}

:deep(.el-sub-menu .el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1) !important;
}

/* 主内容区 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航栏 */
.navbar {
  height: 64px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 10;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 4px;
  transition: background 0.3s;
}

.user-avatar:hover {
  background: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
}

/* 内容区域 */
.content {
  flex: 1;
  overflow: auto;
  background: #f0f2f5;
  padding: 24px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
  }

  .sidebar.collapsed {
    left: -200px;
  }

  .username {
    display: none;
  }
}
</style>
