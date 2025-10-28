import { request } from './request'
import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  ApiResponse
} from '@/types/user'

/**
 * 用户登录
 */
export const login = (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
  return request.post('/auth/login', data)
}

/**
 * 用户注册
 */
export const register = (data: RegisterRequest): Promise<ApiResponse<void>> => {
  return request.post('/auth/register', data)
}

/**
 * 用户登出
 */
export const logout = (): Promise<ApiResponse<void>> => {
  return request.post('/auth/logout')
}

/**
 * 健康检查
 */
export const healthCheck = (): Promise<ApiResponse<string>> => {
  return request.get('/auth/health')
}
