import request from '@/utils/request'

// 树木档案模块 /trees

// 分页查询 GET /trees/page?pageNum=&pageSize=&treeCode=&species=&area=&clientId=&healthStatus=&deptId=
export function getTreePage(params) {
  return request.get('/trees/page', { params })
}

// 树木详情 GET /trees/{id}
export function getTree(id) {
  return request.get(`/trees/${id}`)
}

// 名下树木（客户） GET /trees/my?keyword=
export function getMyTrees(params) {
  return request.get('/trees/my', { params })
}

// 生长档案 GET /trees/{id}/archive
export function getTreeArchive(id) {
  return request.get(`/trees/${id}/archive`)
}

// 新增树木 POST /trees
export function addTree(data) {
  return request.post('/trees', data)
}

// 修改树木 PUT /trees/{id}
export function updateTree(id, data) {
  return request.put(`/trees/${id}`, data)
}

// 删除树木 DELETE /trees/{id}
export function deleteTree(id) {
  return request.delete(`/trees/${id}`)
}
