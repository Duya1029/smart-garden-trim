<template>
  <div class="page-container">
    <!-- 搜索区 -->
    <div class="page-card">
      <div class="search-bar">
        <el-input v-model="query.username" placeholder="账号" clearable style="width: 150px" />
        <el-input v-model="query.realName" placeholder="姓名" clearable style="width: 150px" />
        <el-select v-model="query.role" placeholder="角色" clearable style="width: 140px">
          <el-option v-for="r in ROLE_OPTIONS" :key="r.value" :label="r.label" :value="r.value" />
        </el-select>
        <el-tree-select
          v-model="query.deptId"
          :data="deptTree"
          :props="deptProps"
          node-key="id"
          check-strictly
          clearable
          :render-after-expand="false"
          placeholder="部门"
          style="width: 180px"
        />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="page-card">
      <div class="table-toolbar">
        <span class="page-title">账号管理</span>
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增账号</el-button>
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
        <template #role="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'WORKER' ? 'primary' : 'success'" effect="plain">
            {{ roleText(row.role) }}
          </el-tag>
        </template>
        <template #org="{ row }">
          {{ row.role === 'CLIENT' ? row.clientName || '-' : row.deptName || '-' }}
        </template>
        <template #status="{ row }">
          <el-switch
            :model-value="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="(val) => toggleStatus(row, val)"
          />
        </template>
        <template #actions="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </PageTable>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑账号' : '新增账号'" width="560px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="登录账号" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="密码" :prop="form.id ? '' : 'password'">
          <el-input v-model="form.password" type="password" show-password :placeholder="form.id ? '留空则不修改' : '6~20 位'" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option v-for="r in ROLE_OPTIONS" :key="r.value" :label="r.label" :value="r.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.role === 'CLIENT'" label="所属客户" prop="clientId">
          <el-select v-model="form.clientId" placeholder="请选择客户主体" clearable style="width: 100%">
            <el-option v-for="c in clientOptions" :key="c.id" :label="c.clientName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-else label="所属部门" prop="deptId">
          <el-tree-select
            v-model="form.deptId"
            :data="deptTree"
            :props="deptProps"
            node-key="id"
            check-strictly
            clearable
            :render-after-expand="false"
            placeholder="请选择部门"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="邮箱" />
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
import { getUserPage, addUser, updateUser, deleteUser, changeUserStatus, getClientOptions } from '@/api/user'
import { getDeptTree } from '@/api/dept'
import { ROLE_OPTIONS, roleText } from '@/utils'

const deptProps = { label: 'deptName', children: 'children' }

const columns = [
  { prop: 'username', label: '账号', minWidth: 110 },
  { prop: 'realName', label: '姓名', minWidth: 100 },
  { prop: 'role', label: '角色', width: 100, align: 'center', slot: 'role' },
  { label: '所属组织', minWidth: 140, slot: 'org' },
  { prop: 'phone', label: '手机号', minWidth: 120 },
  { prop: 'email', label: '邮箱', minWidth: 160 },
  { prop: 'status', label: '状态', width: 90, align: 'center', slot: 'status' },
  { prop: 'createTime', label: '创建时间', minWidth: 160 },
  { label: '操作', width: 130, align: 'center', fixed: 'right', slot: 'actions' }
]

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const total = ref(0)
const deptTree = ref([])
const clientOptions = ref([])

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  realName: '',
  role: '',
  deptId: null,
  status: null
})

// 加载列表
async function loadData() {
  loading.value = true
  try {
    const res = await getUserPage({ ...query })
    list.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

// 加载部门树与客户下拉
async function loadOptions() {
  const [dept, clients] = await Promise.all([getDeptTree(), getClientOptions()])
  deptTree.value = dept || []
  clientOptions.value = clients || []
}

function handleSearch() {
  query.pageNum = 1
  loadData()
}

function handleReset() {
  Object.assign(query, { pageNum: 1, pageSize: 10, username: '', realName: '', role: '', deptId: null, status: null })
  loadData()
}

// ===== 弹窗表单 =====
const dialogVisible = ref(false)
const formRef = ref(null)
const defaultForm = () => ({
  id: null,
  username: '',
  password: '',
  realName: '',
  role: 'WORKER',
  phone: '',
  email: '',
  deptId: null,
  clientId: null,
  status: 1
})
const form = reactive(defaultForm())

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

function openDialog(row) {
  resetForm()
  if (row) {
    Object.assign(form, {
      id: row.id,
      username: row.username,
      realName: row.realName,
      role: row.role,
      phone: row.phone || '',
      email: row.email || '',
      deptId: row.deptId,
      clientId: row.clientId,
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
  // 新增时密码必填
  if (!form.id && !form.password) {
    ElMessage.warning('请输入密码')
    return
  }
  const payload = {
    username: form.username,
    password: form.password,
    realName: form.realName,
    role: form.role,
    phone: form.phone,
    email: form.email,
    deptId: form.role === 'CLIENT' ? null : form.deptId,
    clientId: form.role === 'CLIENT' ? form.clientId : null,
    status: form.status
  }
  submitting.value = true
  try {
    if (form.id) {
      await updateUser(form.id, payload)
      ElMessage.success('修改成功')
    } else {
      await addUser(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function toggleStatus(row, val) {
  try {
    await changeUserStatus(row.id, val)
    row.status = val
    ElMessage.success(val === 1 ? '已启用' : '已停用')
  } catch (e) {
    // 失败时保持原状态
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除账号「${row.username}」吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

onMounted(() => {
  loadOptions()
  loadData()
})
</script>
