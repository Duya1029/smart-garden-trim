<template>
  <div class="page-container">
    <!-- 搜索区 -->
    <div class="page-card">
      <div class="search-bar">
        <el-input v-model="query.clientName" placeholder="客户名称" clearable style="width: 180px" />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="page-card">
      <div class="table-toolbar">
        <span class="page-title">客户主体</span>
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增客户</el-button>
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
          <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="plain">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
        <template #actions="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </PageTable>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑客户' : '新增客户'" width="520px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="客户名称" prop="clientName">
          <el-input v-model="form.clientName" placeholder="物业 / 果园等主体名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="联系电话" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="客户地址" />
        </el-form-item>
        <el-form-item label="关联账号ID">
          <el-input-number v-model="form.userId" :min="0" placeholder="可选，关联的登录账号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import PageTable from '@/components/PageTable.vue'
import { getClientPage, addClient, updateClient, deleteClient } from '@/api/client'

const columns = [
  { prop: 'clientName', label: '客户名称', minWidth: 160 },
  { prop: 'contactName', label: '联系人', minWidth: 100 },
  { prop: 'contactPhone', label: '联系电话', minWidth: 130 },
  { prop: 'address', label: '地址', minWidth: 180 },
  { prop: 'status', label: '状态', width: 100, align: 'center', slot: 'status' },
  { label: '操作', width: 140, align: 'center', fixed: 'right', slot: 'actions' }
]

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  clientName: ''
})

// 加载客户分页
async function loadData() {
  loading.value = true
  try {
    const res = await getClientPage({ ...query })
    list.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function handleSearch() {
  query.pageNum = 1
  loadData()
}

function handleReset() {
  Object.assign(query, { pageNum: 1, pageSize: 10, clientName: '' })
  loadData()
}

// ===== 弹窗表单 =====
const dialogVisible = ref(false)
const formRef = ref(null)
const defaultForm = () => ({
  id: null,
  clientName: '',
  contactName: '',
  contactPhone: '',
  address: '',
  userId: null,
  status: 1
})
const form = reactive(defaultForm())

const rules = {
  clientName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

function openDialog(row) {
  resetForm()
  if (row) {
    Object.assign(form, {
      id: row.id,
      clientName: row.clientName,
      contactName: row.contactName || '',
      contactPhone: row.contactPhone || '',
      address: row.address || '',
      userId: row.userId ?? null,
      status: row.status
    })
  }
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(form, defaultForm())
  formRef.value?.clearValidate()
}

async function submitForm() {
  await formRef.value.validate()
  const payload = {
    clientName: form.clientName,
    contactName: form.contactName,
    contactPhone: form.contactPhone,
    address: form.address,
    userId: form.userId,
    status: form.status
  }
  submitting.value = true
  try {
    if (form.id) {
      await updateClient(form.id, payload)
      ElMessage.success('修改成功')
    } else {
      await addClient(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除客户「${row.clientName}」吗？`, '提示', { type: 'warning' })
  await deleteClient(row.id)
  ElMessage.success('删除成功')
  loadData()
}
</script>
