/**
 * 用户信息类型
 */
export interface UserInfo {
  id: number
  username: string
  email: string
  phone?: string
  realName?: string
  status: number
  accountNonExpired: boolean
  accountNonLocked: boolean
  credentialsNonExpired: boolean
  enabled: boolean
  lastLoginTime?: string
  lastLoginIp?: string
  createTime: string
  updateTime: string
  remark?: string
}

/**
 * 登录请求
 */
export interface LoginRequest {
  username: string
  password: string
}

/**
 * 注册请求
 */
export interface RegisterRequest {
  username: string
  password: string
  email: string
  phone?: string
  realName?: string
}

/**
 * 登录响应
 */
export interface LoginResponse {
  token: string
  tokenType: string
  userInfo: {
    id: number
    username: string
    email: string
    phone?: string
    realName?: string
    status: number
  }
}

/**
 * 修改密码请求
 */
export interface UpdatePasswordRequest {
  oldPassword: string
  newPassword: string
}

/**
 * API 响应
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}
