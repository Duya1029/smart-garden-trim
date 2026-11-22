<template>
  <div class="page-container">
    <!-- 搜索区 -->
    <div class="page-card">
      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="任务编号/树木编号" clearable style="width: 180px" />
        <el-select v-model="query.status" placeholder="任务状态" clearable style="width: 140px">
          <el-option v-for="s in TASK_STATUS_OPTIONS" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
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
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="page-card">
      <div class="table-toolbar">
        <span class="page-title">任务管理</span>
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
          <el-tag :type="TASK_STATUS_TAG[row.status] || 'info'" effect="plain">
            {{ taskStatusText(row.status) }}
          </el-tag>
        </template>
        <template #actions="{ row }">
          <el-button
            v-if="row.status !== 'DONE'"
            link
            type="primary"
            @click="openAssign(row)"
          >
            改派
          </el-button>
          <el-button
            v-if="row.status === 'SUBMITTED'"
            link
            type="success"
            @click="handleFinish(row)"
          >
            结单
          </el-button>
        </template>
      </PageTable>
    </div>

    <!-- 改派弹窗 -->
    <el-dialog v-model="assignVisible" title="改派任务" width="480px" @closed="resetAssign">
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="100px">
        <el-form-item label="任务编号">
          <span>{{ current?.taskNo || '-' }}</span>
        </el-form-item>
        <el-form-item label="当前人员">
          <span>{{ current?.workerName || '未派单' }}</span>
        </el-form-item>
        <el-form-item label="改派给" prop="workerId">
          <el-select v-model="assignForm.workerId" placeholder="请选择养护人员" style="width: 100%">
            <el-option
              v-for="w in workers"
              :key="w.id"
              :label="w.realName ? `${w.realName}（${w.username}）` : w.username"
              :value="w.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import PageTable from '@/components/PageTable.vue'
import { getTrimTaskPage, assignTask, finishTask } from '@/api/trimTask'
import { getUserPage, getClientOptions } from '@/api/user'
import { TASK_STATUS_OPTIONS, TASK_STATUS_TAG, taskStatusText } from '@/utils'

const columns = [
  { prop: 'taskNo', label: '任务编号', minWidth: 150 },
  { prop: 'treeCode', label: '树木编号', minWidth: 140 },
  { prop: 'species', label: '树种', minWidth: 90 },
  { prop: 'area', label: '区域', minWidth: 100 },
  { prop: 'location', label: '位置', minWidth: 170 },
  { prop: 'workerName', label: '养护人员', minWidth: 110 },
  { prop: 'clientName', label: '客户', minWidth: 120 },
  { prop: 'trimType', label: '修剪类型', minWidth: 110 },
  { prop: 'planTime', label: '计划时间', minWidth: 160 },
  { prop: 'status', label: '状态', width: 110, align: 'center', slot: 'status' },
  { label: '操作', width: 140, align: 'center', fixed: 'right', slot: 'actions' }
]

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const total = ref(0)
const workers = ref([])
const clientOptions = ref([])

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: '',
  workerId: null,
  clientId: null
})

// 加载任务分页
async function loadData() {
  loading.value = true
  try {
    const res = await getTrimTaskPage({ ...query })
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
  Object.assign(query, { pageNum: 1, pageSize: 10, keyword: '', status: '', workerId: null, clientId: null })
  loadData()
}

// ===== 改派弹窗 =====
const assignVisible = ref(false)
const assignFormRef = ref(null)
const current = ref(null)
const assignForm = reactive({ workerId: null })
const assignRules = {
  workerId: [{ required: true, message: '请选择养护人员', trigger: 'change' }]
}

function openAssign(row) {
  resetAssign()
  current.value = row
  assignForm.workerId = row.workerId ?? null
  assignVisible.value = true
}

function resetAssign() {
  assignForm.workerId = null
  current.value = null
  assignFormRef.value?.clearValidate()
}

async function submitAssign() {
  await assignFormRef.value.validate()
  submitting.value = true
  try {
    await assignTask(current.value.id, assignForm.workerId)
    ElMessage.success('改派成功')
    assignVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

// 结单
async function handleFinish(row) {
  await ElMessageBox.confirm(`确认将任务「${row.taskNo}」结单吗？`, '提示', { type: 'warning' })
  await finishTask(row.id)
  ElMessage.success('结单成功')
  loadData()
}
</script>
