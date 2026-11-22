<template>
  <div class="page-container">
    <!-- 搜索区 -->
    <div class="page-card">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="树木编号/树种/位置" clearable style="width: 240px" @keyup.enter="loadData">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" :icon="Search" @click="loadData">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 树木卡片列表 -->
    <div v-loading="loading">
      <div v-if="list.length" class="tree-grid">
        <div v-for="tree in list" :key="tree.id" class="tree-card page-card">
          <el-image
            v-if="tree.imageUrl"
            :src="joinFileUrl(tree.imageUrl)"
            fit="cover"
            class="tree-card__img"
          />
          <div v-else class="tree-card__img tree-card__img--empty">
            <el-icon :size="28"><Location /></el-icon>
          </div>
          <div class="tree-card__body">
            <div class="tree-card__head">
              <span class="tree-card__species">{{ tree.species || '-' }}</span>
              <el-tag :type="HEALTH_STATUS_TAG[tree.healthStatus] || 'info'" effect="plain" size="small">
                {{ healthStatusText(tree.healthStatus) }}
              </el-tag>
            </div>
            <div class="tree-card__item"><span class="detail-label">编号：</span>{{ tree.treeCode || '-' }}</div>
            <div class="tree-card__item"><span class="detail-label">区域：</span>{{ tree.area || '-' }}</div>
            <div class="tree-card__item"><span class="detail-label">位置：</span>{{ tree.location || '-' }}</div>
            <div class="tree-card__item"><span class="detail-label">树龄：</span>{{ tree.treeAge ?? '-' }} 年</div>
            <div class="tree-card__item"><span class="detail-label">修剪次数：</span>{{ tree.trimCount ?? 0 }}</div>
            <div class="tree-card__item"><span class="detail-label">最近修剪：</span>{{ tree.lastTrimTime || '-' }}</div>
            <div class="tree-card__foot">
              <el-button type="primary" @click="openArchive(tree)">生长档案</el-button>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="page-card">
        <el-empty description="暂无树木数据" />
      </div>
    </div>

    <!-- 生长档案弹窗 -->
    <el-dialog v-model="archiveVisible" title="树木生长档案" width="720px">
      <div v-loading="archiveLoading">
        <template v-if="archive">
          <el-descriptions :column="3" border size="small" style="margin-bottom: 16px">
            <el-descriptions-item label="编号">{{ archive.tree?.treeCode || '-' }}</el-descriptions-item>
            <el-descriptions-item label="树种">{{ archive.tree?.species || '-' }}</el-descriptions-item>
            <el-descriptions-item label="树龄">{{ archive.tree?.treeAge ?? '-' }} 年</el-descriptions-item>
          </el-descriptions>

          <!-- 评分趋势 -->
          <div class="section-title">评分趋势</div>
          <BaseChart v-if="archive.scoreTrend && archive.scoreTrend.length" :option="trendOption" height="220px" />
          <el-empty v-else description="暂无评分数据" :image-size="60" />

          <!-- 修剪记录时间线 -->
          <div class="section-title">修剪记录</div>
          <el-timeline v-if="archive.records && archive.records.length">
            <el-timeline-item
              v-for="rec in archive.records"
              :key="rec.recordId"
              :timestamp="rec.actualTrimTime"
            >
              <div class="record-item">
                <div class="record-item__head">
                  <span class="record-item__type">{{ rec.trimType || '-' }}</span>
                  <el-rate v-if="rec.score !== null && rec.score !== undefined" :model-value="Number(rec.score) || 0" disabled allow-half size="small" />
                </div>
                <div class="record-item__text"><span class="detail-label">修剪情况：</span>{{ rec.workContent || '-' }}</div>
                <div v-if="rec.comment" class="record-item__text"><span class="detail-label">客户评语：</span>{{ rec.comment }}</div>
                <div v-if="rec.images && rec.images.length" class="image-grid" style="margin-top: 8px">
                  <el-image
                    v-for="(url, i) in rec.images"
                    :key="i"
                    :src="joinFileUrl(url)"
                    :preview-src-list="rec.images.map((u) => joinFileUrl(u))"
                    :initial-index="i"
                    preview-teleported
                    fit="cover"
                  />
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无修剪记录" :image-size="60" />
        </template>
        <el-empty v-else-if="!archiveLoading" description="暂无数据" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Search, Refresh, Location } from '@element-plus/icons-vue'
import BaseChart from '@/components/BaseChart.vue'
import { getMyTrees, getTreeArchive } from '@/api/tree'
import { HEALTH_STATUS_TAG, healthStatusText, joinFileUrl } from '@/utils'

const loading = ref(false)
const list = ref([])
const keyword = ref('')

// 加载名下树木
async function loadData() {
  loading.value = true
  try {
    list.value = (await getMyTrees(keyword.value ? { keyword: keyword.value } : {})) || []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function handleReset() {
  keyword.value = ''
  loadData()
}

// ===== 生长档案弹窗 =====
const archiveVisible = ref(false)
const archiveLoading = ref(false)
const archive = ref(null)

// 评分趋势折线图
const trendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 40, right: 20, top: 20, bottom: 40 },
  xAxis: { type: 'category', data: (archive.value?.scoreTrend || []).map((i) => i.date), boundaryGap: false },
  yAxis: { type: 'value', min: 0, max: 5 },
  series: [
    {
      name: '评分',
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.15 },
      itemStyle: { color: '#2e7d32' },
      data: (archive.value?.scoreTrend || []).map((i) => i.score)
    }
  ]
}))

async function openArchive(tree) {
  archiveVisible.value = true
  archiveLoading.value = true
  archive.value = null
  try {
    archive.value = await getTreeArchive(tree.id)
  } finally {
    archiveLoading.value = false
  }
}
</script>

<style scoped>
.tree-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 16px;
}
.tree-card {
  margin-bottom: 0;
  display: flex;
  gap: 14px;
  padding: 12px;
}
.tree-card__img {
  width: 96px;
  height: 96px;
  border-radius: 6px;
  flex-shrink: 0;
}
.tree-card__img--empty {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}
.tree-card__body {
  flex: 1;
  min-width: 0;
}
.tree-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}
.tree-card__species {
  font-size: 16px;
  font-weight: 600;
}
.tree-card__item {
  font-size: 13px;
  line-height: 20px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.tree-card__foot {
  margin-top: 8px;
  text-align: right;
}
.section-title {
  font-size: 15px;
  font-weight: 600;
  margin: 16px 0 12px;
}
.record-item__head {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 4px;
}
.record-item__type {
  font-weight: 600;
}
.record-item__text {
  font-size: 13px;
  line-height: 20px;
}
</style>
