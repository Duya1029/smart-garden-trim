import request from '@/utils/request'

// AI 识别模块 /ai

// 提交识别 POST /ai/analyze
// 入参：{ imageUrl, treeId?, taskId?, species?, remark? }
export function analyze(data) {
  return request.post('/ai/analyze', data)
}

// 查询分析结果 GET /ai/result?taskId=
export function getAiResult(params) {
  return request.get('/ai/result', { params })
}

// 获取 AI 建议文本 GET /ai/suggestion?taskId=
export function getAiSuggestion(params) {
  return request.get('/ai/suggestion', { params })
}
