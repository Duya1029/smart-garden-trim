import request from '@/utils/request'

// 统计看板模块 /stat（管理员）

// 总览指标 GET /stat/overview?startDate=&endDate=
export function getOverview(params) {
  return request.get('/stat/overview', { params })
}

// 区域分布 GET /stat/area
export function getAreaStat() {
  return request.get('/stat/area')
}

// 修剪趋势 GET /stat/trend?days=30
export function getTrendStat(params) {
  return request.get('/stat/trend', { params })
}

// 树种分布 GET /stat/species
export function getSpeciesStat() {
  return request.get('/stat/species')
}

// 养护人员工作量 GET /stat/worker?startDate=&endDate=
export function getWorkerStat(params) {
  return request.get('/stat/worker', { params })
}
