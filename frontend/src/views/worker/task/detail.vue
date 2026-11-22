<template>
  <div class="page-container" v-loading="loading">
    <template v-if="task">
      <!-- 任务基本信息 -->
      <div class="page-card">
        <div class="table-toolbar">
          <span class="page-title">任务详情</span>
          <el-tag :type="TASK_STATUS_TAG[task.status] || 'info'" effect="plain">
            {{ taskStatusText(task.status) }}
          </el-tag>
        </div>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="任务编号">{{ task.taskNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="树木编号">{{ task.treeCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="树种">{{ task.species || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ task.area || '-' }}</el-descriptions-item>
          <el-descriptions-item label="位置" :span="2">{{ task.location || '-' }}</el-descriptions-item>
          <el-descriptions-item label="修剪类型">{{ task.trimType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="计划时间">{{ task.planTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ task.clientName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="养护人员">{{ task.workerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ task.finishTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="优先级">{{ task.priority ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="任务说明" :span="3">{{ task.description || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 流程操作按钮 -->
        <div class="action-bar">
          <el-button v-if="task.status === 'PENDING'" type="primary" :loading="acting" @click="handleAccept">
            接单
          </el-button>
          <el-button v-if="task.status === 'ACCEPTED'" type="primary" :loading="acting" @click="handleStart">
            开始修剪
          </el-button>
          <span v-if="task.status === 'PROCESSING'" class="text-muted">任务进行中，请完成现场识别并提交修剪记录</span>
        </div>
      </div>

      <!-- 修剪作业区（修剪中可操作） -->
      <div v-if="task.status === 'PROCESSING'" class="page-card">
        <div class="section-title">现场照片与 AI 识别</div>
        <el-form label-width="90px">
          <el-form-item label="现场照片">
            <ImageUpload v-model="sceneImages" biz-type="trim" :limit="3" tip="上传现场照片后提交 AI 识别" />
          </el-form-item>
          <el-form-item label=" ">
            <el-button type="primary" :icon="MagicStick" :loading="analyzing" @click="handleAnalyze">
              AI 识别与建议
            </el-button>
          </el-form-item>
        </el-form>

        <!-- AI 识别结果 -->
        <div v-if="aiResult" class="ai-result">
          <div class="ai-result__meta">
            <el-tag type="success" effect="plain">置信度 {{ (Number(aiResult.confidence) * 100).toFixed(0) }}%</el-tag>
            <el-tag v-if="aiResult.isMock" type="warning" effect="plain">模拟数据</el-tag>
            <span class="text-muted">模型：{{ aiResult.modelVersion || '-' }}</span>
          </div>
          <BranchMarkCanvas
            :image-url="aiResult.imageUrl"
            :marked-image-url="aiResult.markedImageUrl"
            :cut-branches="aiResult.cutBranches || []"
            :keep-branches="aiResult.keepBranches || []"
          />
          <div class="ai-result__suggestion">
            <div class="section-title">AI 修剪建议</div>
            <div class="pre-wrap">{{ aiResult.suggestion || '-' }}</div>
          </div>
        </div>
      </div>

      <!-- 提交修剪记录 -->
      <div v-if="task.status === 'PROCESSING'" class="page-card">
        <div class="section-title">提交修剪记录</div>
        <el-form ref="submitFormRef" :model="submitForm" :rules="submitRules" label-width="100px">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="修剪类型" prop="trimType">
                <el-select v-model="submitForm.trimType" placeholder="请选择" style="width: 100%">
                  <el-option v-for="t in TRIM_TYPE_OPTIONS" :key="t.value" :label="t.label" :value="t.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="实际修剪时间" prop="actualTrimTime">
                <el-date-picker
                  v-model="submitForm.actualTrimTime"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="剪除枝条数">
                <el-input-number v-model="submitForm.cutBranchCount" :min="0" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="保留枝条数">
                <el-input-number v-model="submitForm.keepBranchCount" :min="0" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="修剪前照片">
                <ImageUpload v-model="beforeImages" biz-type="trim" :limit="3" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="修剪后照片">
                <ImageUpload v-model="afterImages" biz-type="trim" :limit="3" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="修剪情况" prop="workContent">
                <el-input v-model="submitForm.workContent" type="textarea" :rows="3" placeholder="实际修剪情况说明" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="AI 建议">
                <el-input v-model="submitForm.aiSuggestion" type="textarea" :rows="3" placeholder="采纳的 AI 建议文本" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item>
                <el-button type="primary" :loading="submitting" @click="handleSubmit">提交修剪记录</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 已提交记录（只读展示） -->
      <div v-if="task.record" class="page-card">
        <div class="section-title">已提交的修剪记录</div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="修剪类型">{{ task.record.trimType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="实际修剪时间">{{ task.record.actualTrimTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="剪除/保留">
            {{ task.record.cutBranchCount ?? '-' }} / {{ task.record.keepBranchCount ?? '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="评分">
            <el-rate v-if="task.record.score !== null && task.record.score !== undefined" :model-value="Number(task.record.score) || 0" disabled allow-half />
            <span v-else class="text-muted">未评价</span>
          </el-descriptions-item>
          <el-descriptions-item label="修剪情况" :span="2">{{ task.record.workContent || '-' }}</el-descriptions-item>
          <el-descriptions-item label="客户评语" :span="2">{{ task.record.comment || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div v-for="type in Object.keys(IMAGE_TYPE_TEXT)" :key="type">
          <template v-if="recordImagesOf(type).length">
            <div class="section-title">{{ IMAGE_TYPE_TEXT[type] }}</div>
            <div class="image-grid">
              <el-image
                v-for="(img, i) in recordImagesOf(type)"
                :key="i"
                :src="joinFileUrl(img.imageUrl)"
                :preview-src-list="recordImagesOf(type).map((x) => joinFileUrl(x.imageUrl))"
                :initial-index="i"
                preview-teleported
                fit="cover"
              />
            </div>
          </template>
        </div>
      </div>
    </template>

    <el-empty v-else-if="!loading" description="任务不存在" />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { MagicStick } from '@element-plus/icons-vue'
import ImageUpload from '@/components/ImageUpload.vue'
import BranchMarkCanvas from '@/components/BranchMarkCanvas.vue'
import { getTrimTask, acceptTask, startTask, submitTask } from '@/api/trimTask'
import { analyze, getAiResult } from '@/api/ai'
import {
  TASK_STATUS_TAG,
  taskStatusText,
  TRIM_TYPE_OPTIONS,
  IMAGE_TYPE_TEXT,
  nowDateTime,
  joinFileUrl
} from '@/utils'

const route = useRoute()
const router = useRouter()
const taskId = route.params.id

const loading = ref(false)
const acting = ref(false)
const analyzing = ref(false)
const submitting = ref(false)
const task = ref(null)
const aiResult = ref(null)

// 现场/修剪前/修剪后 图片列表
const sceneImages = ref([])
const beforeImages = ref([])
const afterImages = ref([])

const submitFormRef = ref(null)
const submitForm = reactive({
  trimType: '',
  actualTrimTime: nowDateTime(),
  cutBranchCount: 0,
  keepBranchCount: 0,
  workContent: '',
  aiSuggestion: ''
})

const submitRules = {
  trimType: [{ required: true, message: '请选择修剪类型', trigger: 'change' }],
  actualTrimTime: [{ required: true, message: '请选择实际修剪时间', trigger: 'change' }],
  workContent: [{ required: true, message: '请填写修剪情况', trigger: 'blur' }]
}

// 加载任务详情与历史 AI 结果
async function loadData() {
  loading.value = true
  try {
    task.value = await getTrimTask(taskId)
    // 默认修剪类型取任务指派类型
    submitForm.trimType = task.value?.trimType || ''
    // 恢复该任务最近一次 AI 分析结果
    const ai = await getAiResult({ taskId })
    if (ai) {
      aiResult.value = ai
      submitForm.aiSuggestion = ai.suggestion || ''
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

// 只读记录中的图片按类型筛选
function recordImagesOf(type) {
  return (task.value?.record?.images || []).filter((i) => i.imageType === type)
}

// 接单
async function handleAccept() {
  acting.value = true
  try {
    await acceptTask(taskId)
    ElMessage.success('接单成功')
    loadData()
  } finally {
    acting.value = false
  }
}

// 开始修剪
async function handleStart() {
  acting.value = true
  try {
    await startTask(taskId)
    ElMessage.success('已开始修剪')
    loadData()
  } finally {
    acting.value = false
  }
}

// 提交 AI 识别
async function handleAnalyze() {
  if (!sceneImages.value.length) {
    ElMessage.warning('请先上传现场照片')
    return
  }
  analyzing.value = true
  try {
    const res = await analyze({
      imageUrl: sceneImages.value[0].url,
      treeId: task.value.treeId,
      taskId: Number(taskId),
      species: task.value.species
    })
    aiResult.value = res
    submitForm.aiSuggestion = res?.suggestion || ''
    ElMessage.success('识别完成')
  } finally {
    analyzing.value = false
  }
}

// 提交修剪记录
async function handleSubmit() {
  await submitFormRef.value.validate()
  // 汇总各类型图片
  const images = [
    ...sceneImages.value.map((i) => ({ url: i.url, imageType: 'SCENE' })),
    ...beforeImages.value.map((i) => ({ url: i.url, imageType: 'BEFORE' })),
    ...afterImages.value.map((i) => ({ url: i.url, imageType: 'AFTER' })),
    ...(aiResult.value?.markedImageUrl
      ? [{ url: aiResult.value.markedImageUrl, imageType: 'AI_MARKED' }]
      : [])
  ]
  const payload = {
    trimType: submitForm.trimType,
    actualTrimTime: submitForm.actualTrimTime,
    cutBranchCount: submitForm.cutBranchCount,
    keepBranchCount: submitForm.keepBranchCount,
    workContent: submitForm.workContent,
    aiSuggestion: submitForm.aiSuggestion,
    images
  }
  submitting.value = true
  try {
    await submitTask(taskId, payload)
    ElMessage.success('修剪记录已提交')
    await loadData()
    router.push('/worker/task')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.section-title {
  font-size: 15px;
  font-weight: 600;
  margin: 8px 0 12px;
}
.action-bar {
  margin-top: 16px;
}
.ai-result {
  margin-top: 8px;
}
.ai-result__meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
.ai-result__suggestion {
  margin-top: 12px;
}
.pre-wrap {
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
