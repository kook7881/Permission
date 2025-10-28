import request from './request'

/**
 * 用户创建DTO
 */
export interface UserCreateDTO {
  username: string
  password: string
  email: string
  phone?: string
  realName?: string
  deptId?: number
  position?: string
  avatar?: string
  gender?: number
  status: number
  roleIds?: number[]
  remark?: string
}

/**
 * 用户更新DTO
 */
export interface UserUpdateDTO {
  id: number
  email?: string
  phone?: string
  realName?: string
  deptId?: number
  position?: string
  avatar?: string
  gender?: number
  status?: number
  password?: string
  roleIds?: number[]
  remark?: string
}

/**
 * 用户查询DTO
 */
export interface UserQueryDTO {
  username?: string
  email?: string
  phone?: string
  realName?: string
  status?: number
  deptId?: number
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: string
}

/**
 * 用户详细信息VO
 */
export interface UserDetailVO {
  id: number
  username: string
  email: string
  phone?: string
  realName?: string
  deptId?: number
  deptName?: string
  position?: string
  avatar?: string
  gender?: number
  status: number
  roles?: RoleDTO[]
  lastLoginTime?: string
  lastLoginIp?: string
  createTime: string
  updateTime: string
  createBy?: number
  updateBy?: number
  remark?: string
}

/**
 * 角色DTO
 */
export interface RoleDTO {
  id: number
  roleName: string
  roleCode: string
  roleSort?: number
  dataScope?: number
  status: number
  isSystem: boolean
  remark?: string
  createTime: string
  updateTime: string
  createBy?: string
  updateBy?: string
}

/**
 * 密码修改DTO
 */
export interface PasswordChangeDTO {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

/**
 * 分页查询用户列表
 */
export function queryUsers(params: UserQueryDTO) {
  return request({
    url: '/api/users',
    method: 'get',
    params
  })
}

/**
 * 获取用户详细信息
 */
export function getUserDetail(id: number) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

/**
 * 获取当前登录用户信息
 */
export function getCurrentUserInfo() {
  return request({
    url: '/api/users/me',
    method: 'get'
  })
}

/**
 * 创建用户
 */
export function createUser(data: UserCreateDTO) {
  return request({
    url: '/api/users',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 */
export function updateUser(id: number, data: UserUpdateDTO) {
  return request({
    url: `/api/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(id: number) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除用户
 */
export function batchDeleteUsers(ids: number[]) {
  return request({
    url: '/api/users/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 修改用户状态
 */
export function updateUserStatus(id: number, status: number) {
  return request({
    url: `/api/users/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 批量修改用户状态
 */
export function batchUpdateUserStatus(ids: number[], status: number) {
  return request({
    url: '/api/users/batch/status',
    method: 'put',
    data: ids,
    params: { status }
  })
}

/**
 * 检查用户名是否存在
 */
export function checkUsernameExists(username: string, excludeId?: number) {
  return request({
    url: '/api/users/check/username',
    method: 'get',
    params: { username, excludeId }
  })
}

/**
 * 检查邮箱是否存在
 */
export function checkEmailExists(email: string, excludeId?: number) {
  return request({
    url: '/api/users/check/email',
    method: 'get',
    params: { email, excludeId }
  })
}

/**
 * 检查手机号是否存在
 */
export function checkPhoneExists(phone: string, excludeId?: number) {
  return request({
    url: '/api/users/check/phone',
    method: 'get',
    params: { phone, excludeId }
  })
}

/**
 * 重置用户密码（管理员操作）
 */
export function resetPassword(id: number, newPassword: string) {
  return request({
    url: `/api/users/${id}/reset-password`,
    method: 'post',
    params: { newPassword }
  })
}

/**
 * 修改密码（用户自己操作）
 */
export function changePassword(data: PasswordChangeDTO) {
  return request({
    url: '/api/users/change-password',
    method: 'put',
    data
  })
}

/**
 * 分配用户角色
 */
export function assignUserRoles(userId: number, roleIds: number[]) {
  return request({
    url: `/api/users/${userId}/roles`,
    method: 'put',
    data: roleIds
  })
}

/**
 * 获取用户角色列表
 */
export function getUserRoles(userId: number) {
  return request({
    url: `/api/users/${userId}/roles`,
    method: 'get'
  })
}

/**
 * 导出用户（Excel）
 */
export function exportUsers(params: UserQueryDTO) {
  return request({
    url: '/api/users/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 导入用户（Excel）
 */
export function importUsers(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/users/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
