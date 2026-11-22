import request from '@/utils/request'

// 认证模块 /auth

// 登录 POST /auth/login
export function login(data) {
  return request.post('/auth/login', data)
}

// 获取当前登录用户 GET /auth/info
export function getInfo() {
  return request.get('/auth/info')
}

// 退出登录 POST /auth/logout
export function logout() {
  return request.post('/auth/logout')
}

// 修改密码 POST /auth/password
export function updatePassword(data) {
  return request.post('/auth/password', data)
}
