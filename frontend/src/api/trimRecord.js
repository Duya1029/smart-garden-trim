import request from '@/utils/request'

// 修剪记录模块 /trim-records

// 记录分页查询（管理员） GET /trim-records/page?pageNum=&pageSize=&workerId=&clientId=&treeId=&startDate=&endDate=
export function getTrimRecordPage(params) {
  return request.get('/trim-records/page', { params })
}

// 记录详情 GET /trim-records/{id}
export function getTrimRecord(id) {
  return request.get(`/trim-records/${id}`)
}

// 我的记录（客户） GET /trim-records/my?startDate=&endDate=
export function getMyTrimRecords(params) {
  return request.get('/trim-records/my', { params })
}

// 客户评价 POST /trim-records/{id}/evaluate
// 入参：{ score, comment? }
export function evaluateRecord(id, data) {
  return request.post(`/trim-records/${id}/evaluate`, data)
}

// 评分趋势（客户） GET /trim-records/my-trend?months=6
export function getMyTrend(params) {
  return request.get('/trim-records/my-trend', { params })
}
