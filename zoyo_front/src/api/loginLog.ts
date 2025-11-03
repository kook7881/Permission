import request from './request'

/**
 * 登录日志接口
 */

export interface LoginLog {
  id: number
  userId: number | null
  username: string
  loginTime: string
  loginIp: string
  loginLocation: string
  browser: string
  os: string
  deviceType: string
  status: number
  message: string
}

export interface LoginLogQueryParams {
  username?: string
  status?: number
  page?: number
  size?: number
  sortBy?: string
  sortOrder?: string
}

/**
 * 分页查询登录日志
 */
export const queryLoginLogs = (params: LoginLogQueryParams) => {
  return request.get('/login-log/list', { params })
}

/**
 * 获取登录日志详情
 */
export const getLoginLogById = (id: number) => {
  return request.get(`/login-log/${id}`)
}

/**
 * 删除登录日志
 */
export const deleteLoginLog = (id: number) => {
  return request.delete(`/login-log/${id}`)
}

/**
 * 批量删除登录日志
 */
export const batchDeleteLoginLogs = (ids: number[]) => {
  return request.delete('/login-log/batch', { data: ids })
}

/**
 * 清空登录日志
 */
export const clearLoginLogs = () => {
  return request.delete('/login-log/clear')
}
