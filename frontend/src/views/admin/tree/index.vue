<template>
  <div class="page-container">
    <!-- 搜索区 -->
    <div class="page-card">
      <div class="search-bar">
        <el-input v-model="query.treeCode" placeholder="树木编号" clearable style="width: 150px" />
        <el-input v-model="query.species" placeholder="树种" clearable style="width: 130px" />
        <el-input v-model="query.area" placeholder="区域" clearable style="width: 130px" />
        <el-select v-model="query.clientId" placeholder="所属客户" clearable style="width: 160px">
          <el-option v-for="c in clientOptions" :key="c.id" :label="c.clientName" :value="c.id" />
        </el-select>
        <el-select v-model="query.healthStatus" placeholder="健康状态" clearable style="width: 130px">
          <el-option v-for="h in HEALTH_STATUS_OPTIONS" :key="h.value" :label="h.label" :value="h.value" />
        </el-select>
        <el-tree-select
          v-model="query.deptId"
          :data="deptTree"
          :props="deptProps"
          node-key="id"
          check-strictly
          clearable
          :render-after-expand="false"
          placeholder="养护队"
          style="width: 180px"
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="page-card">
      <div class="table-toolbar">
        <span class="page-title">树木档案</span>
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增树木</el-button>
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
        <template #image="{ row }">
          <el-image
            v-if="row.imageUrl"
            :src="joinFileUrl(row.imageUrl)"
            :preview-src-list="[joinFileUrl(row.imageUrl)]"
            preview-teleported
            fit="cover"
            style="width: 56px; height: 40px; border-radius: 4px; cursor: pointer"
          />
          <span v-else class="text-muted">-</span>
        </template>
        <template #health="{ row }">
          <el-tag :type="HEALTH_STATUS_TAG[row.healthStatus] || 'info'" effect="plain">
            {{ healthStatusText(row.healthStatus) }}
          </el-tag>
        </template>
        <template #actions="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </PageTable>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑树木' : '新增树木'" width="720px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="树木编号" prop="treeCode">
              <el-input v-model="form.treeCode" placeholder="如 TREE-20260001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="树种" prop="species">
              <el-input v-model="form.species" placeholder="如 香樟" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品种">
              <el-input v-model="form.variety" placeholder="如 大叶樟" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区域">
              <el-input v-model="form.area" placeholder="如 城东片区" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="位置">
              <el-input v-model="form.location" placeholder="详细位置" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经度">
              <el-input-number v-model="form.longitude" :precision="6" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度">
              <el-input-number v-model="form.latitude" :precision="6" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="树龄">
              <el-input-number v-model="form.treeAge" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="栽种时间">
              <el-date-picker v-model="form.plantTime" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="树高(m)">
              <el-input-number v-model="form.height" :min="0" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="冠幅(m)">
              <el-input-number v-model="form.crownDiameter" :min="0" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状态">
              <el-select v-model="form.healthStatus" style="width: 100%">
                <el-option v-for="h in HEALTH_STATUS_OPTIONS" :key="h.value" :label="h.label" :value="h.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属客户">
              <el-select v-model="form.clientId" clearable placeholder="请选择客户" style="width: 100%">
                <el-option v-for="c in clientOptions" :key="c.id" :label="c.clientName" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="养护队">
              <el-tree-select
                v-model="form.deptId"
                :data="deptTree"
                :props="deptProps"
                node-key="id"
                check-strictly
                clearable
                :render-after-expand="false"
                placeholder="请选择养护队"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="树木照片">
              <ImageUpload v-model="form.imageList" biz-type="tree" :limit="1" tip="支持 jpg/png/webp，单张不超过 10MB" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" />
            </el-form-item>
          </el-col>
        </el-row>
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
import ImageUpload from '@/components/ImageUpload.vue'
import { getTreePage, addTree, updateTree, deleteTree } from '@/api/tree'
import { getDeptTree } from '@/api/dept'
import { getClientOptions } from '@/api/user'
import {
  HEALTH_STATUS_OPTIONS,
  HEALTH_STATUS_TAG,
  healthStatusText,
  joinFileUrl
} from '@/utils'

const deptProps = { label: 'deptName', children: 'children' }

const columns = [
  { prop: 'treeCode', label: '树木编号', minWidth: 140 },
  { prop: 'species', label: '树种', minWidth: 90 },
  { prop: 'variety', label: '品种', minWidth: 90 },
  { prop: 'area', label: '区域', minWidth: 100 },
  { prop: 'location', label: '位置', minWidth: 180 },
  { label: '照片', width: 90, align: 'center', slot: 'image' },
  { prop: 'healthStatus', label: '健康状态', width: 110, align: 'center', slot: 'health' },
  { prop: 'treeAge', label: '树龄', width: 80, align: 'center' },
  { prop: 'clientName', label: '所属客户', minWidth: 120 },
  { prop: 'deptName', label: '养护队', minWidth: 130 },
  { prop: 'lastTrimTime', label: '最近修剪', minWidth: 160 },
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
  treeCode: '',
  species: '',
  area: '',
  clientId: null,
  healthStatus: '',
  deptId: null
})

// 加载树木分页
async function loadData() {
  loading.value = true
  try {
    const res = await getTreePage({ ...query })
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

onMounted(() => {
  loadOptions()
  loadData()
})

function handleSearch() {
  query.pageNum = 1
  loadData()
}

function handleReset() {
  Object.assign(query, {
    pageNum: 1,
    pageSize: 10,
    treeCode: '',
    species: '',
    area: '',
    clientId: null,
    healthStatus: '',
    deptId: null
  })
  loadData()
}

// ===== 弹窗表单 =====
const dialogVisible = ref(false)
const formRef = ref(null)
const defaultForm = () => ({
  id: null,
  treeCode: '',
  species: '',
  variety: '',
  area: '',
  location: '',
  longitude: null,
  latitude: null,
  treeAge: null,
  plantTime: '',
  height: null,
  crownDiameter: null,
  healthStatus: 'HEALTHY',
  clientId: null,
  deptId: null,
  remark: '',
  imageList: []
})
const form = reactive(defaultForm())

const rules = {
  treeCode: [{ required: true, message: '请输入树木编号', trigger: 'blur' }],
  species: [{ required: true, message: '请输入树种', trigger: 'blur' }]
}

function openDialog(row) {
  resetForm()
  if (row) {
    Object.assign(form, {
      id: row.id,
      treeCode: row.treeCode,
      species: row.species || '',
      variety: row.variety || '',
      area: row.area || '',
      location: row.location || '',
      longitude: row.longitude ?? null,
      latitude: row.latitude ?? null,
      treeAge: row.treeAge ?? null,
      plantTime: row.plantTime || '',
      height: row.height ?? null,
      crownDiameter: row.crownDiameter ?? null,
      healthStatus: row.healthStatus || 'HEALTHY',
      clientId: row.clientId ?? null,
      deptId: row.deptId ?? null,
      remark: row.remark || '',
      imageList: row.imageUrl ? [{ name: '', url: row.imageUrl }] : []
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
    treeCode: form.treeCode,
    species: form.species,
    variety: form.variety,
    area: form.area,
    location: form.location,
    longitude: form.longitude,
    latitude: form.latitude,
    treeAge: form.treeAge,
    plantTime: form.plantTime,
    height: form.height,
    crownDiameter: form.crownDiameter,
    healthStatus: form.healthStatus,
    clientId: form.clientId,
    deptId: form.deptId,
    imageUrl: form.imageList[0]?.url || '',
    remark: form.remark
  }
  submitting.value = true
  try {
    if (form.id) {
      await updateTree(form.id, payload)
      ElMessage.success('修改成功')
    } else {
      await addTree(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除树木「${row.treeCode}」吗？`, '提示', { type: 'warning' })
  await deleteTree(row.id)
  ElMessage.success('删除成功')
  loadData()
}
</script>
