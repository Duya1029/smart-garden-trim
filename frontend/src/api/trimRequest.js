import request from '@/utils/request'

// 修剪需求模块 /trim-requests

// 客户发起需求 POST /trim-requests
// 入参：{ treeId, appointTime, location, trimType?, remark? }
export function addTrimRequest(data) {
  return request.post('/trim-requests', data)
}

// 需求分页查询（管理员） GET /trim-requests/page?pageNum=&pageSize=&status=&clientId=&keyword=
export function getTrimRequestPage(params) {
  return request.get('/trim-requests/page', { params })
}

// 我的需求（客户） GET /trim-requests/my?status=
export function getMyTrimRequests(params) {
  return request.get('/trim-requests/my', { params })
}

// 审核派单（管理员） PUT /trim-requests/{id}/audit
// 入参：{ status, workerId?, planTime?, auditRemark? }
export function auditTrimRequest(id, data) {
  return request.put(`/trim-requests/${id}/audit`, data)
}

// 取消需求（客户） PUT /trim-requests/{id}/cancel
export function cancelTrimRequest(id) {
  return request.put(`/trim-requests/${id}/cancel`)
}

// 删除需求（管理员） DELETE /trim-requests/{id}
export function deleteTrimRequest(id) {
  return request.delete(`/trim-requests/${id}`)
}
