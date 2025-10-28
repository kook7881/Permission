import { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'

/**
 * 权限指令
 * 用法：
 * v-permission="['system:user:delete']" - 单个权限
 * v-permission="['system:user:delete', 'system:user:update']" - 多个权限（OR逻辑）
 * v-permission.and="['system:user:delete', 'system:user:update']" - 多个权限（AND逻辑）
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value, modifiers } = binding
    const userStore = useUserStore()
    
    // 获取用户权限列表
    const permissions = userStore.permissions || []
    
    if (value && Array.isArray(value) && value.length > 0) {
      const requiredPermissions = value as string[]
      
      let hasPermission = false
      
      if (modifiers.and) {
        // AND逻辑：需要拥有所有权限
        hasPermission = requiredPermissions.every(permission => 
          permissions.includes(permission)
        )
      } else {
        // OR逻辑（默认）：拥有任意一个权限即可
        hasPermission = requiredPermissions.some(permission => 
          permissions.includes(permission)
        )
      }
      
      // 如果没有权限，移除元素
      if (!hasPermission) {
        el.parentNode?.removeChild(el)
      }
    } else {
      // 如果没有传入权限要求，默认显示
      console.warn('v-permission指令需要传入权限数组')
    }
  }
}

/**
 * 注册权限指令
 */
export function setupPermissionDirective(app: any) {
  app.directive('permission', permission)
}

export default permission
