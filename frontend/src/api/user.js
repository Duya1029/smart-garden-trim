import request from '@/utils/request'

// 账号管理模块 /users

// 分页查询 GET /users/page?pageNum=&pageSize=&username=&realName=&role=&deptId=&status=
export function getUserPage(params) {
  return request.get('/users/page', { params })
}

// 账号详情 GET /users/{id}
export function getUser(id) {
  return request.get(`/users/${id}`)
}

// 新增账号 POST /users
export function addUser(data) {
  return request.post('/users', data)
}

// 修改账号 PUT /users/{id}
export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

// 删除账号 DELETE /users/{id}
export function deleteUser(id) {
  return request.delete(`/users/${id}`)
}

// 启用/停用 PUT /users/{id}/status?status=1
export function changeUserStatus(id, status) {
  return request.put(`/users/${id}/status`, null, { params: { status } })
}

// 客户下拉列表 GET /users/clients
export function getClientOptions() {
  return request.get('/users/clients')
}
