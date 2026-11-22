<template>
  <div class="page-container">
    <!-- 组织架构树 -->
    <div class="page-card">
      <div class="table-toolbar">
        <span class="page-title">组织架构</span>
        <div>
          <el-button type="primary" :icon="Plus" @click="openDialog(null, null)">新增根部门</el-button>
          <el-button :icon="Refresh" @click="loadData">刷新</el-button>
        </div>
      </div>

      <!-- 部门/养护队树：节点内联展示操作按钮 -->
      <el-tree
        v-if="treeData.length"
        :data="treeData"
        :props="treeProps"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
      >
        <template #default="{ data }">
          <div class="dept-node">
            <span class="dept-node__label">
              <el-tag size="small" :type="data.deptType === 'FLEET' ? 'success' : 'primary'" effect="plain">
                {{ DEPT_TYPE_TEXT[data.deptType] || data.deptType }}
              </el-tag>
              <span class="dept-node__name">{{ data.deptName }}</span>
              <span v-if="data.leader" class="text-muted">负责人：{{ data.leader }}</span>
              <span v-if="data.phone" class="text-muted">电话：{{ data.phone }}</span>
            </span>
            <span class="dept-node__actions">
              <el-button link type="primary" @click.stop="openDialog(null, data)">新增子部门</el-button>
              <el-button link type="primary" @click.stop="openDialog(data, null)">编辑</el-button>
              <el-button link type="danger" @click.stop="handleDelete(data)">删除</el-button>
            </span>
          </div>
        </template>
      </el-tree>
      <el-empty v-else description="暂无组织数据" />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="form.parentId"
            :data="treeData"
            :props="treeProps"
            node-key="id"
            check-strictly
            clearable
            :render-after-expand="false"
            placeholder="不选择则为根部门"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="部门 / 养护队名称" />
        </el-form-item>
        <el-form-item label="类型" prop="deptType">
          <el-select v-model="form.deptType" style="width: 100%">
            <el-option v-for="(text, val) in DEPT_TYPE_TEXT" :key="val" :label="text" :value="val" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leader" placeholder="负责人姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="联系电话" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { getDeptTree, addDept, updateDept, deleteDept } from '@/api/dept'
import { DEPT_TYPE_TEXT } from '@/utils'

// 树节点字段映射
const treeProps = { label: 'deptName', children: 'children' }

const treeData = ref([])

// 加载组织树
async function loadData() {
  treeData.value = (await getDeptTree()) || []
}

onMounted(loadData)

// ===== 弹窗表单 =====
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const defaultForm = () => ({
  id: null,
  parentId: null,
  deptName: '',
  deptType: 'DEPT',
  leader: '',
  phone: '',
  sort: 0
})
const form = reactive(defaultForm())

const dialogTitle = computed(() => (form.id ? '编辑部门' : '新增部门'))

const rules = {
  deptName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  deptType: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

// 打开弹窗：row 为编辑对象；parent 为“新增子部门”时的父节点
function openDialog(row, parent) {
  resetForm()
  if (row) {
    Object.assign(form, {
      id: row.id,
      parentId: row.parentId || null,
      deptName: row.deptName,
      deptType: row.deptType,
      leader: row.leader || '',
      phone: row.phone || '',
      sort: row.sort ?? 0
    })
  } else {
    form.parentId = parent ? parent.id : null
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
    parentId: form.parentId || 0,
    deptName: form.deptName,
    deptType: form.deptType,
    leader: form.leader,
    phone: form.phone,
    sort: form.sort
  }
  submitting.value = true
  try {
    if (form.id) {
      await updateDept(form.id, payload)
      ElMessage.success('修改成功')
    } else {
      await addDept(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

// 删除：后端会校验子节点与关联用户
async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除「${row.deptName}」吗？`, '提示', { type: 'warning' })
  await deleteDept(row.id)
  ElMessage.success('删除成功')
  loadData()
}
</script>

<style scoped>
.dept-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding-right: 8px;
}
.dept-node__label {
  display: flex;
  align-items: center;
  gap: 8px;
}
.dept-node__name {
  font-weight: 500;
}
.dept-node__actions {
  opacity: 0;
  transition: opacity 0.2s;
}
.dept-node:hover .dept-node__actions {
  opacity: 1;
}
</style>
