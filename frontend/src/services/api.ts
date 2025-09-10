import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { message } from 'antd'

// 创建axios实例
const apiClient: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 添加认证token
    const token = localStorage.getItem('auth-storage')
    if (token) {
      try {
        const authData = JSON.parse(token)
        if (authData.state?.token) {
          config.headers.Authorization = `Bearer ${authData.state.token}`
        }
      } catch (error) {
        console.error('解析token失败:', error)
      }
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    // 统一处理成功响应
    return response
  },
  (error) => {
    // 统一处理错误响应
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('auth-storage')
          window.location.href = '/login'
          message.error('登录已过期，请重新登录')
          break
        case 403:
          message.error('权限不足')
          break
        case 404:
          message.error('请求的资源不存在')
          break
        case 500:
          message.error('服务器内部错误')
          break
        default:
          message.error(data?.message || '请求失败')
      }
    } else if (error.request) {
      message.error('网络连接失败，请检查网络设置')
    } else {
      message.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

// 通用请求方法
export const request = {
  get: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> =>
    apiClient.get(url, config).then(res => res.data),
    
  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> =>
    apiClient.post(url, data, config).then(res => res.data),
    
  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> =>
    apiClient.put(url, data, config).then(res => res.data),
    
  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> =>
    apiClient.delete(url, config).then(res => res.data),
    
  patch: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> =>
    apiClient.patch(url, data, config).then(res => res.data),
}

// 文件上传方法
export const uploadFile = (file: File, onProgress?: (progress: number) => void): Promise<string> => {
  const formData = new FormData()
  formData.append('file', file)
  
  return apiClient.post('/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    onUploadProgress: (progressEvent) => {
      if (progressEvent.total && onProgress) {
        const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        onProgress(progress)
      }
    },
  }).then(res => res.data.url)
}

// 批量文件上传
export const uploadFiles = (files: File[], onProgress?: (progress: number) => void): Promise<string[]> => {
  const formData = new FormData()
  files.forEach((file, index) => {
    formData.append(`files`, file)
  })
  
  return apiClient.post('/upload/batch', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    onUploadProgress: (progressEvent) => {
      if (progressEvent.total && onProgress) {
        const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        onProgress(progress)
      }
    },
  }).then(res => res.data.urls)
}

// 成长记录相关API
export const growthRecordAPI = {
  // 创建成长记录
  createRecord: (data: any) => 
    request.post('/growth-record/create', data),
    
  // 获取宝宝成长记录
  getBabyRecords: (babyId: number, page: number, size: number) => 
    request.get(`/growth-record/baby/${babyId}?page=${page}&size=${size}`),
    
  // 按类型获取记录
  getRecordsByType: (babyId: number, type: string) => 
    request.get(`/growth-record/baby/${babyId}/type/${type}`),
    
  // 获取所有宝宝
  getBabies: () => 
    request.get('/family/my-families'),
}

// 教育计划相关API
export const educationPlanAPI = {
  // 创建教育计划
  createPlan: (data: any) => 
    request.post('/education-plan/create', data),
    
  // 获取宝宝教育计划
  getBabyPlans: (babyId: number, page: number = 0, size: number = 10) => 
    request.get(`/education-plan/baby/${babyId}?page=${page}&size=${size}`),
    
  // 获取进行中的计划
  getActivePlans: (babyId: number) => 
    request.get(`/education-plan/baby/${babyId}/active`),
    
  // 启动计划
  startPlan: (planId: number) => 
    request.post(`/education-plan/${planId}/start`),
    
  // 完成计划
  completePlan: (planId: number) => 
    request.post(`/education-plan/${planId}/complete`),
    
  // 创建教育活动
  createActivity: (data: any) => 
    request.post('/education-plan/activity/create', data),
    
  // 获取计划活动
  getPlanActivities: (planId: number, page: number = 0, size: number = 20) => 
    request.get(`/education-plan/${planId}/activities?page=${page}&size=${size}`),
    
  // 完成活动
  completeActivity: (activityId: number, data: { notes?: string, rating?: number }) => 
    request.post(`/education-plan/activity/${activityId}/complete`, data),
}

// 家庭相关API
export const familyAPI = {
  // 获取我的家庭列表
  getMyFamilies: () => 
    request.get('/family/my-families'),
    
  // 获取家庭宝宝列表
  getFamilyBabies: (familyId: number) => 
    request.get(`/family/${familyId}/babies`),
}

// 家庭动态相关API
export const familyPostAPI = {
  // 创建家庭动态
  createPost: (data: any) => 
    request.post('/family-post/create', data),
    
  // 获取家庭动态
  getFamilyPosts: (familyId: number, page: number = 0, size: number = 20) => 
    request.get(`/family-post/family/${familyId}?page=${page}&size=${size}`),
    
  // 点赞动态
  likePost: (postId: number) => 
    request.post(`/family-post/${postId}/like`),
    
  // 取消点赞
  unlikePost: (postId: number) => 
    request.delete(`/family-post/${postId}/like`),
}

// 家庭任务相关API
export const familyTaskAPI = {
  // 创建家庭任务
  createTask: (data: any) => 
    request.post('/family-task/create', data),
    
  // 获取家庭任务
  getFamilyTasks: (familyId: number, page: number = 0, size: number = 20) => 
    request.get(`/family-task/family/${familyId}?page=${page}&size=${size}`),
    
  // 获取我的任务
  getMyTasks: (page: number = 0, size: number = 20) => 
    request.get(`/family-task/my-tasks?page=${page}&size=${size}`),
    
  // 开始任务
  startTask: (taskId: number) => 
    request.post(`/family-task/${taskId}/start`),
    
  // 完成任务
  completeTask: (taskId: number, data: { completionNotes?: string }) => 
    request.post(`/family-task/${taskId}/complete`, data),
    
  // 取消任务
  cancelTask: (taskId: number) => 
    request.post(`/family-task/${taskId}/cancel`),
}

export default apiClient