import { request } from './request'

/**
 * 操作日志接口
 */

// 操作日志查询参数
export interface OperationLogQuery {
  username?: string
  module?: string
  businessType?: number
  status?: number
  startTime?: string
  endTime?: string
  page?: number
  size?: number
  sortBy?: string
  sortOrder?: string
}

// 操作日志实体
export interface OperationLog {
  id: number
  module: string
  businessType: number
  businessTypes?: string
  description: string
  method: string
  requestMethod: string
  userId: number
  username: string
  realName?: string
  requestUrl: string
  requestIp: string
  requestLocation?: string
  requestParam?: string
  responseResult?: string
  status: number
  errorMsg?: string
  operationTime: string
  executionTime?: number
  browser?: string
  os?: string
  deviceType?: string
}

/**
 * 分页查询操作日志
 */
export function getOperationLogList(params: OperationLogQuery) {
  return request.get('/operation-log/list', { params })
}

/**
 * 获取操作日志详情
 */
export function getOperationLogById(id: number) {
  return request.get(`/operation-log/${id}`)
}

/**
 * 根据用户ID查询操作日志
 */
export function getOperationLogsByUserId(userId: number) {
  return request.get(`/operation-log/user/${userId}`)
}

/**
 * 删除操作日志
 */
export function deleteOperationLog(id: number) {
  return request.delete(`/operation-log/${id}`)
}

/**
 * 批量删除操作日志
 */
export function batchDeleteOperationLogs(ids: number[]) {
  return request.delete('/operation-log/batch', { data: ids })
}

/**
 * 清空操作日志
 */
export function clearOperationLogs() {
  return request.delete('/operation-log/clear')
}

/**
 * 清理历史操作日志
 */
export function cleanOperationLogs(beforeTime: string) {
  return request.delete('/operation-log/clean', { params: { beforeTime } })
}

/**
 * 统计用户操作次数
 */
export function countUserOperations(userId: number) {
  return request.get(`/operation-log/count/user/${userId}`)
}
