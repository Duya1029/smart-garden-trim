// ============ 枚举中文映射（严格对齐接口文档 1.5 节） ============

// 角色 role
export const ROLE_TEXT = {
  ADMIN: '管理员',
  WORKER: '养护人员',
  CLIENT: '客户'
}
// 角色下拉选项
export const ROLE_OPTIONS = [
  { value: 'ADMIN', label: '管理员' },
  { value: 'WORKER', label: '养护人员' },
  { value: 'CLIENT', label: '客户' }
]

// 需求状态 trim_request.status
export const REQUEST_STATUS_TEXT = {
  PENDING: '待审核',
  AUDITED: '已审核待派单',
  DISPATCHED: '已派单',
  COMPLETED: '已完成',
  REJECTED: '已驳回',
  CANCELLED: '已取消'
}
export const REQUEST_STATUS_TAG = {
  PENDING: 'warning',
  AUDITED: 'primary',
  DISPATCHED: 'primary',
  COMPLETED: 'success',
  REJECTED: 'danger',
  CANCELLED: 'info'
}
export const REQUEST_STATUS_OPTIONS = Object.keys(REQUEST_STATUS_TEXT).map((k) => ({
  value: k,
  label: REQUEST_STATUS_TEXT[k]
}))

// 任务状态 trim_task.status
export const TASK_STATUS_TEXT = {
  PENDING: '待接单',
  ACCEPTED: '已接单',
  PROCESSING: '修剪中',
  SUBMITTED: '已提交',
  DONE: '已完成'
}
export const TASK_STATUS_TAG = {
  PENDING: 'warning',
  ACCEPTED: 'primary',
  PROCESSING: 'primary',
  SUBMITTED: 'success',
  DONE: 'success'
}
export const TASK_STATUS_OPTIONS = Object.keys(TASK_STATUS_TEXT).map((k) => ({
  value: k,
  label: TASK_STATUS_TEXT[k]
}))

// 树木健康状态 tree.health_status
export const HEALTH_STATUS_TEXT = {
  HEALTHY: '健康',
  SUB_HEALTHY: '亚健康',
  SICK: '病虫害',
  DEAD: '枯死'
}
export const HEALTH_STATUS_TAG = {
  HEALTHY: 'success',
  SUB_HEALTHY: 'warning',
  SICK: 'danger',
  DEAD: 'info'
}
export const HEALTH_STATUS_OPTIONS = Object.keys(HEALTH_STATUS_TEXT).map((k) => ({
  value: k,
  label: HEALTH_STATUS_TEXT[k]
}))

// 图片类型 trim_record_image.image_type
export const IMAGE_TYPE_TEXT = {
  SCENE: '现场照片',
  BEFORE: '修剪前',
  AFTER: '修剪后',
  AI_MARKED: 'AI标注图'
}

// 修剪记录状态（文档 11.1 示例 status = EVALUATED）
export const RECORD_STATUS_TEXT = {
  PENDING: '待评价',
  EVALUATED: '已评价'
}

// 部门类型
export const DEPT_TYPE_TEXT = {
  DEPT: '部门',
  FLEET: '养护队'
}

// 常用修剪类型选项
export const TRIM_TYPE_OPTIONS = [
  '整形修剪',
  '疏枝修剪',
  '造型修剪',
  '病虫枝清除',
  '枯枝清理',
  '果树修剪'
].map((v) => ({ value: v, label: v }))

// ============ 文本转换辅助 ============

export function roleText(v) {
  return ROLE_TEXT[v] || v || '-'
}
export function requestStatusText(v) {
  return REQUEST_STATUS_TEXT[v] || v || '-'
}
export function taskStatusText(v) {
  return TASK_STATUS_TEXT[v] || v || '-'
}
export function healthStatusText(v) {
  return HEALTH_STATUS_TEXT[v] || v || '-'
}
export function imageTypeText(v) {
  return IMAGE_TYPE_TEXT[v] || v || '-'
}
export function recordStatusText(v) {
  return RECORD_STATUS_TEXT[v] || v || '-'
}

// 评分展示：无评分时显示 '-'
export function scoreText(v) {
  return v === null || v === undefined || v === '' ? '-' : Number(v).toFixed(1)
}

// 拼接图片访问地址：相对路径补上 VITE_FILE_BASE
export function joinFileUrl(url) {
  if (!url) return ''
  if (/^https?:\/\//i.test(url)) return url
  const base = import.meta.env.VITE_FILE_BASE || ''
  return base + url
}

// 从图片对象数组按类型取第一张的完整地址
export function findImageUrl(images, type) {
  if (!Array.isArray(images)) return ''
  const hit = images.find((i) => i.imageType === type)
  return hit ? joinFileUrl(hit.imageUrl) : ''
}

// 生成 yyyy-MM-dd HH:mm:ss 字符串（用于提交实际修剪时间等）
export function nowDateTime() {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(
    d.getMinutes()
  )}:${p(d.getSeconds())}`
}

// 生成 yyyy-MM-dd 字符串
export function formatDate(d) {
  if (!d) return ''
  const date = d instanceof Date ? d : new Date(d)
  if (Number.isNaN(date.getTime())) return ''
  const p = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${p(date.getMonth() + 1)}-${p(date.getDate())}`
}

// 时间字符串格式化：直接原样返回后端字符串（后端已为 yyyy-MM-dd HH:mm:ss）
export function formatDateTime(v) {
  return v || '-'
}
