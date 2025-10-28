import request from './request'

/**
 * 权限管理API
 */

/**
 * 获取权限树
 */
export const getPermissionTree = () => {
  return request({
    url: '/permission/tree',
    method: 'get'
  })
}

/**
 * 获取权限列表
 */
export const getPermissionList = () => {
  return request({
    url: '/permission/list',
    method: 'get'
  })
}

/**
 * 获取权限详情
 */
export const getPermissionById = (id: number) => {
  return request({
    url: `/permission/${id}`,
    method: 'get'
  })
}

/**
 * 创建权限
 */
export const createPermission = (data: {
  parentId?: number
  permissionName: string
  permissionCode: string
  permissionType: number
  routePath?: string
  componentPath?: string
  icon?: string
  sortOrder?: number
  visible?: boolean
  status?: number
  remark?: string
}) => {
  return request({
    url: '/permission/create',
    method: 'post',
    data
  })
}

/**
 * 更新权限
 */
export const updatePermission = (id: number, data: {
  parentId?: number
  permissionName: string
  permissionCode: string
  permissionType: number
  routePath?: string
  componentPath?: string
  icon?: string
  sortOrder?: number
  visible?: boolean
  status?: number
  remark?: string
}) => {
  return request({
    url: `/permission/update/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除权限
 */
export const deletePermission = (id: number) => {
  return request({
    url: `/permission/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 获取当前用户权限列表
 */
export const getUserPermissions = () => {
  return request({
    url: '/permission/user-permissions',
    method: 'get'
  })
}

/**
 * 获取当前用户菜单树
 */
export const getUserMenus = () => {
  return request({
    url: '/permission/user-menus',
    method: 'get'
  })
}
