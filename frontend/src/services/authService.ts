import { request } from './api'
import { User, LoginForm, RegisterForm, ApiResponse } from '../types'

export interface AuthAPI {
  login: (data: LoginForm) => Promise<ApiResponse<{ user: User; token: string }>>
  register: (data: RegisterForm) => Promise<ApiResponse<{ user: User; token: string }>>
  logout: () => Promise<ApiResponse>
  refreshToken: () => Promise<ApiResponse<{ token: string }>>
  updateProfile: (data: Partial<User>) => Promise<ApiResponse<User>>
  changePassword: (data: { oldPassword: string; newPassword: string }) => Promise<ApiResponse>
  sendVerificationCode: (phone: string) => Promise<ApiResponse>
  verifyPhone: (phone: string, code: string) => Promise<ApiResponse>
  resetPassword: (email: string) => Promise<ApiResponse>
}

// 认证相关API
export const authAPI: AuthAPI = {
  // 用户登录
  login: (data: LoginForm) =>
    request.post('/auth/login', data),

  // 用户注册
  register: (data: RegisterForm) =>
    request.post('/auth/register', data),

  // 用户登出
  logout: () =>
    request.post('/auth/logout'),

  // 刷新token
  refreshToken: () =>
    request.post('/auth/refresh'),

  // 更新用户资料
  updateProfile: (data: Partial<User>) =>
    request.put('/auth/profile', data),

  // 修改密码
  changePassword: (data: { oldPassword: string; newPassword: string }) =>
    request.put('/auth/change-password', data),

  // 发送验证码
  sendVerificationCode: (phone: string) =>
    request.post('/auth/send-code', { phone }),

  // 验证手机号
  verifyPhone: (phone: string, code: string) =>
    request.post('/auth/verify-phone', { phone, code }),

  // 重置密码
  resetPassword: (email: string) =>
    request.post('/auth/reset-password', { email }),
}