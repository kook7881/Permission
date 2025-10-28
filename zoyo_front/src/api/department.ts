import request from './request'

/**
 * 部门DTO
 */
export interface DepartmentDTO {
  id?: number
  name: string
  parentId: number
  code?: string
  sort: number
  leaderId?: number
  leaderName?: string
  phone?: string
  email?: string
  status: number
  createTime?: string
  updateTime?: string
  remark?: string
}

/**
 * 部门树形结构VO
 */
export interface DepartmentTreeVO {
  id: number
  name: string
  parentId: number
  code?: string
  sort: number
  leaderId?: number
  leaderName?: string
  phone?: string
  email?: string
  status: number
  children?: DepartmentTreeVO[]
  remark?: string
}

/**
 * 获取部门树
 */
export function getDepartmentTree() {
  return request({
    url: '/api/departments/tree',
    method: 'get'
  })
}

/**
 * 获取所有部门列表
 */
export function getAllDepartments() {
  return request({
    url: '/api/departments',
    method: 'get'
  })
}

/**
 * 根据ID获取部门
 */
export function getDepartmentById(id: number) {
  return request({
    url: `/api/departments/${id}`,
    method: 'get'
  })
}

/**
 * 创建部门
 */
export function createDepartment(data: DepartmentDTO) {
  return request({
    url: '/api/departments',
    method: 'post',
    data
  })
}

/**
 * 更新部门
 */
export function updateDepartment(id: number, data: DepartmentDTO) {
  return request({
    url: `/api/departments/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除部门
 */
export function deleteDepartment(id: number) {
  return request({
    url: `/api/departments/${id}`,
    method: 'delete'
  })
}

/**
 * 获取子部门列表
 */
export function getChildDepartments(parentId: number) {
  return request({
    url: `/api/departments/${parentId}/children`,
    method: 'get'
  })
}

/**
 * 检查部门下是否有用户
 */
export function checkDepartmentHasUsers(id: number) {
  return request({
    url: `/api/departments/${id}/has-users`,
    method: 'get'
  })
}

/**
 * 检查部门下是否有子部门
 */
export function checkDepartmentHasChildren(id: number) {
  return request({
    url: `/api/departments/${id}/has-children`,
    method: 'get'
  })
}
