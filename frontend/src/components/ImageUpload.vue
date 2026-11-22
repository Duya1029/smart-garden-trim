<template>
  <div class="image-upload">
    <el-upload
      v-if="list.length < limit"
      class="image-upload__trigger"
      :show-file-list="false"
      :http-request="doUpload"
      :before-upload="beforeUpload"
      :limit="limit"
      :on-exceed="onExceed"
      accept="image/jpeg,image/jpg,image/png,image/webp"
    >
      <el-button type="primary" :icon="Plus" :loading="uploading">上传图片</el-button>
    </el-upload>

    <div v-if="list.length" class="image-upload__list">
      <div v-for="(item, index) in list" :key="index" class="image-upload__item">
        <el-image
          :src="joinFileUrl(item.url)"
          fit="cover"
          :preview-src-list="previewList"
          :initial-index="index"
          preview-teleported
        />
        <el-icon class="image-upload__del" @click="remove(index)"><Close /></el-icon>
      </div>
    </div>

    <div v-else class="image-upload__empty">
      <el-empty description="暂无图片" :image-size="60" />
    </div>
    <div v-if="tip" class="image-upload__tip">{{ tip }}</div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Close } from '@element-plus/icons-vue'
import { uploadFile } from '@/api/file'
import { joinFileUrl } from '@/utils'

// 图片上传组件：调用 /files/upload，v-model 绑定 [{name,url}] 数组
const props = defineProps({
  // 已上传图片列表 [{name, url}]
  modelValue: { type: Array, default: () => [] },
  // 业务类型：tree / trim / ai
  bizType: { type: String, default: '' },
  // 最大数量
  limit: { type: Number, default: 6 },
  // 提示文案
  tip: { type: String, default: '' }
})
const emit = defineEmits(['update:modelValue'])

const uploading = ref(false)
const list = ref([])

// 与 v-model 双向同步
watch(
  () => props.modelValue,
  (val) => {
    // 避免自身 emit 引发的循环更新
    if (val !== list.value) {
      list.value = Array.isArray(val) ? [...val] : []
    }
  },
  { immediate: true, deep: true }
)

const previewList = computed(() => list.value.map((i) => joinFileUrl(i.url)))

// 上传前校验类型与大小
function beforeUpload(file) {
  const allow = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp']
  if (!allow.includes(file.type)) {
    ElMessage.error('仅支持 jpg / jpeg / png / webp 格式')
    return false
  }
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('单张图片不能超过 10MB')
    return false
  }
  return true
}

// 超出数量限制提示
function onExceed() {
  ElMessage.warning(`最多上传 ${props.limit} 张图片`)
}

// 自定义上传：走 /files/upload
async function doUpload(options) {
  uploading.value = true
  const formData = new FormData()
  formData.append('file', options.file)
  if (props.bizType) {
    formData.append('bizType', props.bizType)
  }
  try {
    const res = await uploadFile(formData)
    const next = [...list.value, { name: res.name, url: res.url }]
    list.value = next
    emit('update:modelValue', next)
    ElMessage.success('上传成功')
  } catch (e) {
    // 错误已由请求拦截器统一提示
  } finally {
    uploading.value = false
  }
}

// 删除图片
function remove(index) {
  const next = [...list.value]
  next.splice(index, 1)
  list.value = next
  emit('update:modelValue', next)
}
</script>

<style scoped>
.image-upload__list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}
.image-upload__item {
  position: relative;
  width: 96px;
  height: 96px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid var(--sg-border-color);
}
.image-upload__item :deep(.el-image) {
  width: 100%;
  height: 100%;
}
.image-upload__del {
  position: absolute;
  right: 2px;
  top: 2px;
  cursor: pointer;
  color: #fff;
  background: rgba(0, 0, 0, 0.45);
  border-radius: 50%;
  padding: 2px;
  font-size: 14px;
}
.image-upload__empty {
  margin-top: 8px;
  border: 1px dashed var(--sg-border-color);
  border-radius: 4px;
}
.image-upload__tip {
  margin-top: 6px;
  font-size: 12px;
  color: var(--sg-text-secondary);
}
</style>
