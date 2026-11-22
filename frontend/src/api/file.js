import request from '@/utils/request'

// 文件上传模块 /files

// 上传图片 POST /files/upload（multipart/form-data）
// formData 需包含 file 字段，可选 bizType：tree / trim / ai
export function uploadFile(formData) {
  return request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
