import request from '@/utils/request'

// 修剪任务模块 /trim-tasks

// 任务分页查询 GET /trim-tasks/page?pageNum=&pageSize=&status=&workerId=&clientId=&keyword=
export function getTrimTaskPage(params) {
  return request.get('/trim-tasks/page', { params })
}

// 我的任务（养护人员） GET /trim-tasks/my?status=
export function getMyTrimTasks(params) {
  return request.get('/trim-tasks/my', { params })
}

// 任务详情 GET /trim-tasks/{id}
export function getTrimTask(id) {
  return request.get(`/trim-tasks/${id}`)
}

// 接单 PUT /trim-tasks/{id}/accept
export function acceptTask(id) {
  return request.put(`/trim-tasks/${id}/accept`)
}

// 开始修剪 PUT /trim-tasks/{id}/start
export function startTask(id) {
  return request.put(`/trim-tasks/${id}/start`)
}

// 提交修剪记录 PUT /trim-tasks/{id}/submit
// 入参：{ trimType, actualTrimTime, cutBranchCount?, keepBranchCount?, workContent, aiSuggestion?, images? }
export function submitTask(id, data) {
  return request.put(`/trim-tasks/${id}/submit`, data)
}

// 改派任务（管理员） PUT /trim-tasks/{id}/assign?workerId=3
export function assignTask(id, workerId) {
  return request.put(`/trim-tasks/${id}/assign`, null, { params: { workerId } })
}

// 结单（管理员） PUT /trim-tasks/{id}/finish
export function finishTask(id) {
  return request.put(`/trim-tasks/${id}/finish`)
}
