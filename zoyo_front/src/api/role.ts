import request from './request'

/**
 * 角色管理API
 */

/**
 * 获取角色列表（分页）
 */
export const getRoleList = (params: {
  roleName?: string
  status?: number
  page?: number
  size?: number
}) => {
  return request({
    url: '/role/list',
    method: 'get',
    params
  })
}

/**
 * 获取所有角色（不分页）
 */
export const getAllRoles = () => {
  return request({
    url: '/role/all',
    method: 'get'
  })
}

/**
 * 获取角色详情
 */
export const getRoleById = (id: number) => {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

/**
 * 创建角色
 */
export const createRole = (data: {
  roleName: string
  roleCode: string
  roleSort?: number
  dataScope?: number
  status?: number
  remark?: string
}) => {
  return request({
    url: '/role/create',
    method: 'post',
    data
  })
}

/**
 * 更新角色
 */
export const updateRole = (id: number, data: {
  roleName: string
  roleCode: string
  roleSort?: number
  dataScope?: number
  status?: number
  remark?: string
}) => {
  return request({
    url: `/role/update/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除角色
 */
export const deleteRole = (id: number) => {
  return request({
    url: `/role/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除角色
 */
export const batchDeleteRoles = (ids: number[]) => {
  return request({
    url: '/role/batch-delete',
    method: 'delete',
    data: ids
  })
}

/**
 * 更新角色状态
 */
export const updateRoleStatus = (id: number, status: number) => {
  return request({
    url: `/role/status/${id}`,
    method: 'put',
    data: { status }
  })
}

/**
 * 获取角色权限
 */
export const getRolePermissions = (id: number) => {
  return request({
    url: `/role/${id}/permissions`,
    method: 'get'
  })
}

/**
 * 分配角色权限
 */
export const assignRolePermissions = (id: number, permissionIds: number[]) => {
  return request({
    url: `/role/${id}/permissions`,
    method: 'post',
    data: permissionIds
  })
}
