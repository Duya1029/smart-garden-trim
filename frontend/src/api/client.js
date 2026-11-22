import request from '@/utils/request'

// 客户主体模块 /clients

// 分页查询 GET /clients/page?pageNum=&pageSize=&clientName=
export function getClientPage(params) {
  return request.get('/clients/page', { params })
}

// 新增客户 POST /clients
export function addClient(data) {
  return request.post('/clients', data)
}

// 修改客户 PUT /clients/{id}
export function updateClient(id, data) {
  return request.put(`/clients/${id}`, data)
}

// 删除客户 DELETE /clients/{id}
export function deleteClient(id) {
  return request.delete(`/clients/${id}`)
}
