import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

/**
 * Axios 实例 —— 所有 API 请求的基础配置
 *
 * 功能：
 * 1. 统一 baseURL（开发环境通过 Vite 代理转发到后端）
 * 2. 请求拦截器：自动附加 JWT Token
 * 3. 响应拦截器：统一处理错误（401=跳转登录，403=无权限提示）
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// ==================== 请求拦截器 ====================
request.interceptors.request.use(
  config => {
    // 从 localStorage 获取 Token，附加到请求头
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// ==================== 响应拦截器 ====================
request.interceptors.response.use(
  response => {
    // 后端统一返回 { code, message, data }
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    const status = error.response?.status
    if (status === 401) {
      // Token 过期或无效 → 清除本地数据 → 跳转登录页
      localStorage.clear()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else if (status === 403) {
      ElMessage.error('权限不足')
    } else {
      ElMessage.error(error.response?.data?.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
