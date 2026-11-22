import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'

// 用户信息本地缓存 key（与 store/user 保持一致，用于 401 时清理登录态）
const USER_INFO_KEY = 'smart_garden_user'

// 创建 Axios 实例：baseURL 取环境变量 VITE_API_BASE
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 30000
})

// 请求拦截器：注入 Authorization: Bearer <token>
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 未登录统一处理：清空本地登录态并跳转登录页
function handleUnauthorized(msg) {
  removeToken()
  localStorage.removeItem(USER_INFO_KEY)
  if (window.location.pathname.indexOf('/login') === -1) {
    ElMessage.error(msg || '登录已过期，请重新登录')
    import('@/router').then(({ default: router }) => {
      router.replace('/login')
    })
  }
}

// 响应拦截器：统一解包 {code,msg,data}
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 非标准响应体（如文件流）直接返回
    if (!res || typeof res !== 'object' || !('code' in res)) {
      return res
    }
    const { code, msg, data } = res
    if (code === 200) {
      // 成功：仅把 data 交给业务代码
      return data
    }
    if (code === 401) {
      handleUnauthorized(msg)
      return Promise.reject(new Error(msg || '未登录'))
    }
    if (code === 403) {
      ElMessage.error(msg || '无权限访问')
      return Promise.reject(new Error(msg || '无权限访问'))
    }
    // 500 及其他：弹出 msg
    ElMessage.error(msg || '请求失败')
    return Promise.reject(new Error(msg || '请求失败'))
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      handleUnauthorized()
      return Promise.reject(error)
    }
    const msg = error.response?.data?.msg || error.message || '网络异常，请稍后重试'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default service
