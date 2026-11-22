<template>
  <div class="page-container">
    <!-- 状态筛选 -->
    <div class="page-card">
      <div class="search-bar">
        <span class="detail-label">任务状态：</span>
        <el-select v-model="status" placeholder="全部" clearable style="width: 160px" @change="loadData">
          <el-option v-for="s in TASK_STATUS_OPTIONS" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-button :icon="Refresh" @click="loadData">刷新</el-button>
      </div>
    </div>

    <!-- 任务卡片列表 -->
    <div v-loading="loading">
      <div v-if="list.length" class="task-grid">
        <div v-for="task in list" :key="task.id" class="task-card page-card">
          <div class="task-card__head">
            <span class="task-card__no">{{ task.taskNo }}</span>
            <el-tag :type="TASK_STATUS_TAG[task.status] || 'info'" effect="plain">
              {{ taskStatusText(task.status) }}
            </el-tag>
          </div>
          <div class="task-card__body">
            <div class="task-card__title">
              <span>{{ task.species || '-' }}</span>
              <span class="text-muted">{{ task.treeCode || '-' }}</span>
            </div>
            <div class="task-card__item"><span class="detail-label">区域：</span>{{ task.area || '-' }}</div>
            <div class="task-card__item"><span class="detail-label">位置：</span>{{ task.location || '-' }}</div>
            <div class="task-card__item"><span class="detail-label">修剪类型：</span>{{ task.trimType || '-' }}</div>
            <div class="task-card__item"><span class="detail-label">计划时间：</span>{{ task.planTime || '-' }}</div>
            <div class="task-card__item"><span class="detail-label">客户：</span>{{ task.clientName || '-' }}</div>
          </div>
          <div class="task-card__foot">
            <el-button type="primary" @click="goDetail(task.id)">查看 / 处理</el-button>
          </div>
        </div>
      </div>
      <div v-else class="page-card">
        <el-empty description="暂无任务" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import { getMyTrimTasks } from '@/api/trimTask'
import { TASK_STATUS_OPTIONS, TASK_STATUS_TAG, taskStatusText } from '@/utils'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const status = ref('')

// 加载我的任务（按状态过滤）
async function loadData() {
  loading.value = true
  try {
    list.value = (await getMyTrimTasks(status.value ? { status: status.value } : {})) || []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function goDetail(id) {
  router.push(`/worker/task/${id}`)
}
</script>

<style scoped>
.task-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}
.task-card {
  margin-bottom: 0;
}
.task-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.task-card__no {
  font-weight: 600;
}
.task-card__title {
  display: flex;
  align-items: baseline;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
}
.task-card__item {
  font-size: 13px;
  line-height: 22px;
}
.task-card__foot {
  margin-top: 12px;
  text-align: right;
}
</style>
