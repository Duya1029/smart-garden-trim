<template>
  <div class="page-container">
    <!-- 搜索区 -->
    <div class="page-card">
      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="需求编号/树种" clearable style="width: 170px" />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 150px">
          <el-option v-for="s in REQUEST_STATUS_OPTIONS" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-select v-model="query.clientId" placeholder="客户" clearable style="width: 160px">
          <el-option v-for="c in clientOptions" :key="c.id" :label="c.clientName" :value="c.id" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="page-card">
      <div class="table-toolbar">
        <span class="page-title">修剪需求审核</span>
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
        <template #status="{ row }">
          <el-tag :type="REQUEST_STATUS_TAG[row.status] || 'info'" effect="plain">
            {{ requestStatusText(row.status) }}
          </el-tag>
        </template>
        <template #actions="{ row }">
          <el-button
            v-if="row.status === 'PENDING' || row.status === 'AUDITED'"
            link
            type="primary"
            @click="openAudit(row)"
          >
            {{ row.status === 'PENDING' ? '审核' : '派单' }}
          </el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </PageTable>
    </div>

    <!-- 审核派单弹窗 -->
    <el-dialog v-model="auditVisible" title="审核派单" width="560px" @closed="resetAudit">
      <el-descriptions v-if="current" :column="2" border size="small" style="margin-bottom: 16px">
        <el-descriptions-item label="需求编号">{{ current.requestNo }}</el-descriptions-item>
        <el-descriptions-item label="树种">{{ current.species || '-' }}</el-descriptions-item>
        <el-descriptions-item label="树木编号">{{ current.treeCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ current.clientName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ current.appointTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="地点">{{ current.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="期望类型">{{ current.trimType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ current.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" label-width="100px">
        <el-form-item label="审核结果" prop="status">
          <el-radio-group v-model="auditForm.status">
            <el-radio value="AUDITED">通过并派单</el-radio>
            <el-radio value="REJECTED">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="auditForm.status === 'AUDITED'">
          <el-form-item label="养护人员" prop="workerId">
            <el-select v-model="auditForm.workerId" placeholder="请选择养护人员" style="width: 100%">
              <el-option
                v-for="w in workers"
                :key="w.id"
                :label="w.realName ? `${w.realName}（${w.username}）` : w.username"
                :value="w.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="计划时间">
            <el-date-picker
              v-model="auditForm.planTime"
              type="datetime"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="缺省取客户预约时间"
              style="width: 100%"
            />
          </el-form-item>
        </template>
        <el-form-item label="审核意见">
          <el-input v-model="auditForm.auditRemark" type="textarea" :rows="2" placeholder="审核意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitAudit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import PageTable from '@/components/PageTable.vue'
import { getTrimRequestPage, auditTrimRequest, deleteTrimRequest } from '@/api/trimRequest'
import { getUserPage, getClientOptions } from '@/api/user'
import { REQUEST_STATUS_OPTIONS, REQUEST_STATUS_TAG, requestStatusText } from '@/utils'

const columns = [
  { prop: 'requestNo', label: '需求编号', minWidth: 150 },
  { prop: 'treeCode', label: '树木编号', minWidth: 140 },
  { prop: 'species', label: '树种', minWidth: 90 },
  { prop: 'clientName', label: '客户', minWidth: 120 },
  { prop: 'appointTime', label: '预约时间', minWidth: 160 },
  { prop: 'location', label: '地点', minWidth: 170 },
  { prop: 'trimType', label: '期望类型', minWidth: 110 },
  { prop: 'status', label: '状态', width: 130, align: 'center', slot: 'status' },
  { prop: 'createTime', label: '发起时间', minWidth: 160 },
  { label: '操作', width: 150, align: 'center', fixed: 'right', slot: 'actions' }
]

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const total = ref(0)
const clientOptions = ref([])
const workers = ref([])

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: '',
  clientId: null
})

// 加载需求分页
async function loadData() {
  loading.value = true
  try {
    const res = await getTrimRequestPage({ ...query })
    list.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

// 加载客户下拉与养护人员列表（用于派单）
async function loadOptions() {
  const [clients, workerRes] = await Promise.all([
    getClientOptions(),
    getUserPage({ pageNum: 1, pageSize: 100, role: 'WORKER', status: 1 })
  ])
  clientOptions.value = clients || []
  workers.value = workerRes.list || []
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
  Object.assign(query, { pageNum: 1, pageSize: 10, keyword: '', status: '', clientId: null })
  loadData()
}

// ===== 审核弹窗 =====
const auditVisible = ref(false)
const auditFormRef = ref(null)
const current = ref(null)
const defaultAudit = () => ({ status: 'AUDITED', workerId: null, planTime: '', auditRemark: '' })
const auditForm = reactive(defaultAudit())

const auditRules = {
  status: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  workerId: [
    {
      validator: (rule, value, callback) => {
        if (auditForm.status === 'AUDITED' && !value) {
          callback(new Error('请选择派单的养护人员'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

function openAudit(row) {
  resetAudit()
  current.value = row
  // 已审核待派单默认带出原计划时间
  auditForm.planTime = row.status === 'AUDITED' ? row.appointTime : ''
  auditVisible.value = true
}

function resetAudit() {
  Object.assign(auditForm, defaultAudit())
  current.value = null
  auditFormRef.value?.clearValidate()
}

async function submitAudit() {
  await auditFormRef.value.validate()
  const payload = {
    status: auditForm.status,
    auditRemark: auditForm.auditRemark
  }
  if (auditForm.status === 'AUDITED') {
    payload.workerId = auditForm.workerId
    payload.planTime = auditForm.planTime || ''
  }
  submitting.value = true
  try {
    const res = await auditTrimRequest(current.value.id, payload)
    ElMessage.success(auditForm.status === 'AUDITED' ? `派单成功${res?.taskNo ? '：' + res.taskNo : ''}` : '已驳回')
    auditVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除需求「${row.requestNo}」吗？`, '提示', { type: 'warning' })
  await deleteTrimRequest(row.id)
  ElMessage.success('删除成功')
  loadData()
}
</script>
