<template>
  <div class="page-container">
    <!-- 发起修剪需求 -->
    <div class="page-card">
      <div class="page-title" style="margin-bottom: 16px">发起修剪需求</div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="选择树木" prop="treeId">
              <el-select v-model="form.treeId" placeholder="请选择需修剪的树木" style="width: 100%" @change="onTreeChange">
                <el-option
                  v-for="t in myTrees"
                  :key="t.id"
                  :label="`${t.species || ''} ${t.treeCode || ''}（${t.location || '-'}）`"
                  :value="t.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约时间" prop="appointTime">
              <el-date-picker
                v-model="form.appointTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择预约时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="修剪地点" prop="location">
              <el-input v-model="form.location" placeholder="默认取树木位置" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期望类型">
              <el-select v-model="form.trimType" placeholder="请选择修剪类型" clearable style="width: 100%">
                <el-option v-for="t in TRIM_TYPE_OPTIONS" :key="t.value" :label="t.label" :value="t.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="补充说明（选填）" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="submitRequest">提交需求</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 我的需求列表 -->
    <div class="page-card">
      <div class="table-toolbar">
        <span class="page-title">我的修剪需求</span>
        <el-button :icon="Refresh" @click="loadList">刷新</el-button>
      </div>
      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="requestNo" label="需求编号" min-width="150" />
        <el-table-column prop="treeCode" label="树木编号" min-width="130" />
        <el-table-column prop="species" label="树种" min-width="90" />
        <el-table-column prop="appointTime" label="预约时间" min-width="160" />
        <el-table-column prop="location" label="地点" min-width="170" />
        <el-table-column prop="trimType" label="期望类型" min-width="110" />
        <el-table-column label="状态" width="130" align="center">
          <template #default="{ row }">
            <el-tag :type="REQUEST_STATUS_TAG[row.status] || 'info'" effect="plain">
              {{ requestStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditRemark" label="审核意见" min-width="150" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PENDING' || row.status === 'AUDITED'"
              link
              type="danger"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无需求" />
        </template>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getMyTrees } from '@/api/tree'
import { addTrimRequest, getMyTrimRequests, cancelTrimRequest } from '@/api/trimRequest'
import { TRIM_TYPE_OPTIONS, REQUEST_STATUS_TAG, requestStatusText } from '@/utils'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const myTrees = ref([])

const formRef = ref(null)
const form = reactive({
  treeId: null,
  appointTime: '',
  location: '',
  trimType: '',
  remark: ''
})

const rules = {
  treeId: [{ required: true, message: '请选择树木', trigger: 'change' }],
  appointTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  location: [{ required: true, message: '请输入修剪地点', trigger: 'blur' }]
}

// 选择树木后自动带出位置
function onTreeChange(id) {
  const tree = myTrees.value.find((t) => t.id === id)
  if (tree) {
    form.location = tree.location || ''
  }
}

// 加载名下树木
async function loadTrees() {
  myTrees.value = (await getMyTrees()) || []
}

// 加载我的需求
async function loadList() {
  loading.value = true
  try {
    list.value = (await getMyTrimRequests()) || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTrees()
  loadList()
})

async function submitRequest() {
  await formRef.value.validate()
  submitting.value = true
  try {
    const res = await addTrimRequest({ ...form })
    ElMessage.success(`需求提交成功：${res?.requestNo || ''}`)
    // 重置表单并刷新列表
    Object.assign(form, { treeId: null, appointTime: '', location: '', trimType: '', remark: '' })
    formRef.value?.clearValidate()
    loadList()
  } finally {
    submitting.value = false
  }
}

async function handleCancel(row) {
  await ElMessageBox.confirm(`确认取消需求「${row.requestNo}」吗？`, '提示', { type: 'warning' })
  await cancelTrimRequest(row.id)
  ElMessage.success('已取消')
  loadList()
}
</script>
