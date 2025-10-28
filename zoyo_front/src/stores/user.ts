import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  
  // 从 localStorage 恢复 userInfo
  const storedUserInfo = localStorage.getItem('userInfo')
  const userInfo = ref<UserInfo | null>(storedUserInfo ? JSON.parse(storedUserInfo) : null)
  
  // 从 localStorage 恢复 permissions
  const storedPermissions = localStorage.getItem('permissions')
  const permissions = ref<string[]>(storedPermissions ? JSON.parse(storedPermissions) : [])
  
  // 从 localStorage 恢复 roles
  const storedRoles = localStorage.getItem('roles')
  const roles = ref<any[]>(storedRoles ? JSON.parse(storedRoles) : [])
  
  // 从 localStorage 恢复 menus
  const storedMenus = localStorage.getItem('menus')
  const menus = ref<any[]>(storedMenus ? JSON.parse(storedMenus) : [])

  // 设置 Token
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  // 设置权限
  const setPermissions = (perms: string[]) => {
    permissions.value = perms
    localStorage.setItem('permissions', JSON.stringify(perms))
  }

  // 设置角色
  const setRoles = (userRoles: any[]) => {
    roles.value = userRoles
    localStorage.setItem('roles', JSON.stringify(userRoles))
  }

  // 设置菜单
  const setMenus = (userMenus: any[]) => {
    menus.value = userMenus
    localStorage.setItem('menus', JSON.stringify(userMenus))
  }

  // 检查是否有权限
  const hasPermission = (permission: string) => {
    return permissions.value.includes(permission)
  }

  // 检查是否有任意一个权限
  const hasAnyPermission = (perms: string[]) => {
    return perms.some(p => permissions.value.includes(p))
  }

  // 检查是否有所有权限
  const hasAllPermissions = (perms: string[]) => {
    return perms.every(p => permissions.value.includes(p))
  }

  // 清除登录信息
  const clearAuth = () => {
    token.value = ''
    userInfo.value = null
    permissions.value = []
    roles.value = []
    menus.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('permissions')
    localStorage.removeItem('roles')
    localStorage.removeItem('menus')
  }

  return {
    token,
    userInfo,
    permissions,
    roles,
    menus,
    setToken,
    setUserInfo,
    setPermissions,
    setRoles,
    setMenus,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    clearAuth
  }
})
