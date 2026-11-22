import request from '@/utils/request'

// 组织架构模块 /depts（管理员）

// 部门/养护队树 GET /depts/tree
export function getDeptTree() {
  return request.get('/depts/tree')
}

// 部门列表（平铺） GET /depts/list
export function getDeptList(params) {
  return request.get('/depts/list', { params })
}

// 新增部门 POST /depts
export function addDept(data) {
  return request.post('/depts', data)
}

// 修改部门 PUT /depts/{id}
export function updateDept(id, data) {
  return request.put(`/depts/${id}`, data)
}

// 删除部门 DELETE /depts/{id}
export function deleteDept(id) {
  return request.delete(`/depts/${id}`)
}
