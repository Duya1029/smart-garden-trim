<template>
  <div class="page-container">
    <!-- 筛选区 -->
    <div class="page-card">
      <div class="search-bar">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 260px"
        />
        <el-button type="primary" :icon="Search" @click="loadData">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 修剪报告列表 -->
    <div class="page-card">
      <div class="table-toolbar">
        <span class="page-title">修剪报告</span>
      </div>
      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="taskNo" label="任务编号" min-width="150" />
        <el-table-column prop="treeCode" label="树木编号" min-width="130" />
        <el-table-column prop="species" label="树种" min-width="90" />
        <el-table-column prop="trimType" label="修剪类型" min-width="110" />
        <el-table-column prop="actualTrimTime" label="修剪时间" min-width="160" />
        <el-table-column prop="workerName" label="养护人员" min-width="110" />
        <el-table-column label="评分" width="140" align="center">
          <template #default="{ row }">
            <el-rate
              v-if="row.score !== null && row.score !== undefined"
              :model-value="Number(row.score) || 0"
              disabled
              allow-half
            />
            <span v-else class="text-muted">未评价</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'EVALUATED' ? 'success' : 'warning'" effect="plain">
              {{ recordStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button
              v-if="row.status !== 'EVALUATED'"
              link
              type="success"
              @click="openEvaluate(row)"
            >
              评价
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无修剪报告" />
        </template>
      </el-table>
    </div>

    <!-- 详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="修剪报告详情" size="600px">
      <div v-loading="detailLoading">
        <template v-if="detail">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="任务编号">{{ detail.taskNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="树木编号">{{ detail.treeCode || '-' }}</el-descriptions-item>
            <el-descriptions-item label="树种">{{ detail.species || '-' }}</el-descriptions-item>
            <el-descriptions-item label="修剪类型">{{ detail.trimType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="位置" :span="2">{{ detail.location || '-' }}</el-descriptions-item>
            <el-descriptions-item label="实际修剪时间">{{ detail.actualTrimTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="养护人员">{{ detail.workerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="剪除/保留">
              {{ detail.cutBranchCount ?? '-' }} / {{ detail.keepBranchCount ?? '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="评分">
              <el-rate v-if="detail.score !== null && detail.score !== undefined" :model-value="Number(detail.score) || 0" disabled allow-half />
              <span v-else class="text-muted">未评价</span>
            </el-descriptions-item>
            <el-descriptions-item label="修剪情况" :span="2">{{ detail.workContent || '-' }}</el-descriptions-item>
            <el-descriptions-item label="客户评语" :span="2">{{ detail.comment || '-' }}</el-descriptions-item>
          </el-descriptions>
          <div v-for="type in Object.keys(IMAGE_TYPE_TEXT)" :key="type">
            <template v-if="imagesOf(type).length">
              <div class="section-title">{{ IMAGE_TYPE_TEXT[type] }}</div>
              <div class="image-grid">
                <el-image
                  v-for="(img, i) in imagesOf(type)"
                  :key="i"
                  :src="joinFileUrl(img.imageUrl)"
                  :preview-src-list="imagesOf(type).map((x) => joinFileUrl(x.imageUrl))"
                  :initial-index="i"
                  preview-teleported
                  fit="cover"
                />
              </div>
            </template>
          </div>
          <el-empty v-if="!detail.images || !detail.images.length" description="暂无图片" :image-size="60" />
        </template>
        <el-empty v-else-if="!detailLoading" description="暂无数据" />
      </div>
    </el-drawer>

    <!-- 评价弹窗 -->
    <el-dialog v-model="evaluateVisible" title="服务评价" width="460px" @closed="resetEvaluate">
      <el-form ref="evaluateFormRef" :model="evaluateForm" :rules="evaluateRules" label-width="80px">
        <el-form-item label="评分" prop="score">
          <el-rate v-model="evaluateForm.score" allow-half />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="evaluateForm.comment" type="textarea" :rows="3" placeholder="请填写您的评价（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evaluateVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitEvaluate">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getMyTrimRecords, getTrimRecord, evaluateRecord } from '@/api/trimRecord'
import { IMAGE_TYPE_TEXT, recordStatusText, joinFileUrl } from '@/utils'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const dateRange = ref([])

// 加载我的修剪记录
async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (dateRange.value?.length) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    list.value = (await getMyTrimRecords(params)) || []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function handleReset() {
  dateRange.value = []
  loadData()
}

// ===== 详情抽屉 =====
const drawerVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)

function imagesOf(type) {
  return (detail.value?.images || []).filter((i) => i.imageType === type)
}

async function openDetail(row) {
  drawerVisible.value = true
  detailLoading.value = true
  detail.value = null
  try {
    detail.value = await getTrimRecord(row.id)
  } finally {
    detailLoading.value = false
  }
}

// ===== 评价弹窗 =====
const evaluateVisible = ref(false)
const evaluateFormRef = ref(null)
const current = ref(null)
const evaluateForm = reactive({ score: 5, comment: '' })
const evaluateRules = {
  score: [{ required: true, message: '请选择评分', trigger: 'change' }]
}

function openEvaluate(row) {
  resetEvaluate()
  current.value = row
  evaluateVisible.value = true
}

function resetEvaluate() {
  evaluateForm.score = 5
  evaluateForm.comment = ''
  current.value = null
  evaluateFormRef.value?.clearValidate()
}

async function submitEvaluate() {
  await evaluateFormRef.value.validate()
  submitting.value = true
  try {
    await evaluateRecord(current.value.id, { score: evaluateForm.score, comment: evaluateForm.comment })
    ElMessage.success('评价成功')
    evaluateVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.section-title {
  font-size: 14px;
  font-weight: 600;
  margin: 16px 0 8px;
}
</style>
