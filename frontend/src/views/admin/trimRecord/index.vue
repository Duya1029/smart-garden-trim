<template>
  <div class="page-container">
    <!-- 搜索区 -->
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
        <el-select v-model="query.workerId" placeholder="养护人员" clearable style="width: 160px">
          <el-option
            v-for="w in workers"
            :key="w.id"
            :label="w.realName ? `${w.realName}（${w.username}）` : w.username"
            :value="w.id"
          />
        </el-select>
        <el-select v-model="query.clientId" placeholder="客户" clearable style="width: 160px">
          <el-option v-for="c in clientOptions" :key="c.id" :label="c.clientName" :value="c.id" />
        </el-select>
        <el-input v-model="query.treeId" placeholder="树木ID" clearable style="width: 120px" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="page-card">
      <div class="table-toolbar">
        <span class="page-title">修剪记录</span>
      </div>

      <PageTable
        :columns="columns"
        :data="list"
        :loading="loading"
        :total="total"
        v-model:pageNum="query.pageNum"
        v-model:pageSize="query.pageSize"
        @change="loadData"
      >
        <template #score="{ row }">
          <div v-if="row.score !== null && row.score !== undefined">
            <el-rate :model-value="Number(row.score) || 0" disabled allow-half />
          </div>
          <span v-else class="text-muted">未评价</span>
        </template>
        <template #status="{ row }">
          <el-tag :type="row.status === 'EVALUATED' ? 'success' : 'warning'" effect="plain">
            {{ recordStatusText(row.status) }}
          </el-tag>
        </template>
        <template #actions="{ row }">
          <el-button link type="primary" @click="openDetail(row)">详情</el-button>
        </template>
      </PageTable>
    </div>

    <!-- 记录详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="修剪记录详情" size="640px">
      <div v-loading="detailLoading">
        <template v-if="detail">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="任务编号">{{ detail.taskNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="树木编号">{{ detail.treeCode || '-' }}</el-descriptions-item>
            <el-descriptions-item label="树种">{{ detail.species || '-' }}</el-descriptions-item>
            <el-descriptions-item label="修剪类型">{{ detail.trimType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="位置" :span="2">{{ detail.location || '-' }}</el-descriptions-item>
            <el-descriptions-item label="养护人员">{{ detail.workerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="客户">{{ detail.clientName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="实际修剪时间">{{ detail.actualTrimTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="剪除/保留">
              {{ detail.cutBranchCount ?? '-' }} / {{ detail.keepBranchCount ?? '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="修剪情况" :span="2">{{ detail.workContent || '-' }}</el-descriptions-item>
            <el-descriptions-item label="评分">
              <el-rate v-if="detail.score !== null && detail.score !== undefined" :model-value="Number(detail.score) || 0" disabled allow-half />
              <span v-else class="text-muted">未评价</span>
            </el-descriptions-item>
            <el-descriptions-item label="评价时间">{{ detail.evaluateTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="客户评语" :span="2">{{ detail.comment || '-' }}</el-descriptions-item>
            <el-descriptions-item label="AI 建议" :span="2">
              <div class="pre-wrap">{{ detail.aiSuggestion || '-' }}</div>
            </el-descriptions-item>
          </el-descriptions>

          <!-- 图片分类型展示（含 AI 标注图） -->
          <div v-for="type in Object.keys(IMAGE_TYPE_TEXT)" :key="type" class="image-section">
            <div v-if="imagesOf(type).length" class="image-section__title">{{ IMAGE_TYPE_TEXT[type] }}</div>
            <div v-if="imagesOf(type).length" class="image-grid">
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
          </div>
          <el-empty v-if="!detail.images || !detail.images.length" description="暂无图片" :image-size="70" />
        </template>
        <el-empty v-else-if="!detailLoading" description="暂无数据" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import PageTable from '@/components/PageTable.vue'
import { getTrimRecordPage, getTrimRecord } from '@/api/trimRecord'
import { getUserPage, getClientOptions } from '@/api/user'
import { IMAGE_TYPE_TEXT, recordStatusText, joinFileUrl } from '@/utils'

const columns = [
  { prop: 'taskNo', label: '任务编号', minWidth: 150 },
  { prop: 'treeCode', label: '树木编号', minWidth: 140 },
  { prop: 'species', label: '树种', minWidth: 90 },
  { prop: 'location', label: '位置', minWidth: 170 },
  { prop: 'workerName', label: '养护人员', minWidth: 110 },
  { prop: 'clientName', label: '客户', minWidth: 120 },
  { prop: 'trimType', label: '修剪类型', minWidth: 110 },
  { prop: 'actualTrimTime', label: '修剪时间', minWidth: 160 },
  { label: '评分', width: 130, align: 'center', slot: 'score' },
  { prop: 'status', label: '状态', width: 100, align: 'center', slot: 'status' },
  { label: '操作', width: 90, align: 'center', fixed: 'right', slot: 'actions' }
]

const loading = ref(false)
const list = ref([])
const total = ref(0)
const workers = ref([])
const clientOptions = ref([])
const dateRange = ref([])

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  workerId: null,
  clientId: null,
  treeId: ''
})

// 加载记录分页
async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    // 空值不传，日期范围拆分处理
    if (!params.workerId) delete params.workerId
    if (!params.clientId) delete params.clientId
    if (!params.treeId) delete params.treeId
    if (dateRange.value?.length) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getTrimRecordPage(params)
    list.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

// 加载养护人员与客户下拉
async function loadOptions() {
  const [workerRes, clients] = await Promise.all([
    getUserPage({ pageNum: 1, pageSize: 100, role: 'WORKER', status: 1 }),
    getClientOptions()
  ])
  workers.value = workerRes.list || []
  clientOptions.value = clients || []
}

onMounted(() => {
  loadOptions()
  loadData()
})

function handleSearch() {
  query.pageNum = 1
  loadData()
}

function handleReset() {
  Object.assign(query, { pageNum: 1, pageSize: 10, workerId: null, clientId: null, treeId: '' })
  dateRange.value = []
  loadData()
}

// ===== 详情抽屉 =====
const drawerVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)

// 按图片类型筛选
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
</script>

<style scoped>
.pre-wrap {
  white-space: pre-wrap;
  word-break: break-word;
}
.image-section {
  margin-top: 16px;
}
.image-section__title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}
</style>
