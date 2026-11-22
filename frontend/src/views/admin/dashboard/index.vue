<template>
  <div class="page-container">
    <!-- 查询条件：时间范围与趋势天数 -->
    <div class="page-card">
      <div class="table-toolbar" style="margin-bottom: 0">
        <div class="search-bar">
          <span class="detail-label">统计时间：</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="loadStat"
          />
          <span class="detail-label">趋势天数：</span>
          <el-select v-model="trendDays" style="width: 120px" @change="loadTrend">
            <el-option :value="7" label="近 7 天" />
            <el-option :value="30" label="近 30 天" />
            <el-option :value="90" label="近 90 天" />
          </el-select>
        </div>
        <el-button type="primary" :icon="Refresh" @click="loadAll">刷新</el-button>
      </div>
    </div>

    <!-- 顶部指标卡 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="8" :md="4" v-for="card in statCards" :key="card.title">
        <StatCard :title="card.title" :value="card.value" :suffix="card.suffix" :icon="card.icon" :color="card.color" />
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <!-- 区域分布饼图 -->
      <el-col :xs="24" :md="12">
        <div class="page-card">
          <div class="chart-title">区域分布</div>
          <BaseChart v-if="areaData.length" :option="areaOption" height="320px" />
          <el-empty v-else description="暂无数据" />
        </div>
      </el-col>
      <!-- 修剪趋势折线 -->
      <el-col :xs="24" :md="12">
        <div class="page-card">
          <div class="chart-title">修剪趋势（近 {{ trendDays }} 天）</div>
          <BaseChart v-if="trendData.length" :option="trendOption" height="320px" />
          <el-empty v-else description="暂无数据" />
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <!-- 树种分布柱状图 -->
      <el-col :xs="24" :md="12">
        <div class="page-card">
          <div class="chart-title">树种分布</div>
          <BaseChart v-if="speciesData.length" :option="speciesOption" height="320px" />
          <el-empty v-else description="暂无数据" />
        </div>
      </el-col>
      <!-- 养护人员工作量表格 -->
      <el-col :xs="24" :md="12">
        <div class="page-card">
          <div class="chart-title">养护人员工作量</div>
          <el-table v-loading="workerLoading" :data="workerData" border stripe max-height="320">
            <el-table-column prop="workerName" label="养护人员" min-width="100" />
            <el-table-column prop="taskCount" label="任务数" width="90" align="center" />
            <el-table-column prop="trimCount" label="修剪棵数" width="100" align="center" />
            <el-table-column label="平均评分" width="110" align="center">
              <template #default="{ row }">
                <el-rate :model-value="Number(row.avgScore) || 0" disabled allow-half />
                <div class="text-muted">{{ scoreText(row.avgScore) }}</div>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="暂无数据" />
            </template>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
import BaseChart from '@/components/BaseChart.vue'
import { getOverview, getAreaStat, getTrendStat, getSpeciesStat, getWorkerStat } from '@/api/stat'
import { scoreText } from '@/utils'

const dateRange = ref([])
const trendDays = ref(30)
const workerLoading = ref(false)

const overview = ref({})
const areaData = ref([])
const trendData = ref([])
const speciesData = ref([])
const workerData = ref([])

// 顶部指标卡数据
const statCards = computed(() => [
  { title: '树木总数', value: overview.value.treeTotal ?? 0, suffix: '棵', icon: 'Location', color: '#2e7d32' },
  { title: '已修剪棵数', value: overview.value.trimmedCount ?? 0, suffix: '棵', icon: 'Scissor', color: '#409eff' },
  { title: '任务总数', value: overview.value.taskTotal ?? 0, suffix: '单', icon: 'Tickets', color: '#909399' },
  { title: '任务完成率', value: overview.value.completionRate ?? 0, suffix: '%', icon: 'TrendCharts', color: '#52c41a' },
  { title: '平均评分', value: scoreText(overview.value.avgScore), suffix: '分', icon: 'Star', color: '#e6a23c' },
  { title: '客户 / 养护人员', value: `${overview.value.clientTotal ?? 0}/${overview.value.workerTotal ?? 0}`, icon: 'User', color: '#722ed1' }
])

// 区域分布饼图
const areaOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0, type: 'scroll' },
  series: [
    {
      type: 'pie',
      radius: ['38%', '64%'],
      center: ['50%', '46%'],
      label: { formatter: '{b}\n{c}' },
      data: areaData.value.map((i) => ({ name: i.area, value: i.treeCount }))
    }
  ]
}))

// 修剪趋势折线图
const trendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 40, right: 20, top: 30, bottom: 40 },
  xAxis: { type: 'category', data: trendData.value.map((i) => i.date), boundaryGap: false },
  yAxis: { type: 'value', minInterval: 1 },
  series: [
    {
      name: '修剪数量',
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.15 },
      itemStyle: { color: '#2e7d32' },
      data: trendData.value.map((i) => i.count)
    }
  ]
}))

// 树种分布柱状图
const speciesOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 40, right: 20, top: 30, bottom: 60 },
  xAxis: {
    type: 'category',
    data: speciesData.value.map((i) => i.species),
    axisLabel: { rotate: 30 }
  },
  yAxis: { type: 'value', minInterval: 1 },
  series: [
    {
      name: '数量',
      type: 'bar',
      barMaxWidth: 40,
      itemStyle: { color: '#4caf50', borderRadius: [4, 4, 0, 0] },
      data: speciesData.value.map((i) => i.count)
    }
  ]
}))

// 加载总览与人员工作量
async function loadStat() {
  const params = dateRange.value?.length
    ? { startDate: dateRange.value[0], endDate: dateRange.value[1] }
    : {}
  const [ov, wk] = await Promise.all([getOverview(params), getWorkerStat(params)])
  overview.value = ov || {}
  workerData.value = wk || []
}

// 加载区域分布与树种分布
async function loadBase() {
  const [area, species] = await Promise.all([getAreaStat(), getSpeciesStat()])
  areaData.value = area || []
  speciesData.value = species || []
}

// 加载修剪趋势
async function loadTrend() {
  const trend = await getTrendStat({ days: trendDays.value })
  trendData.value = trend || []
}

workerLoading.value = true

// 刷新全部数据
async function loadAll() {
  workerLoading.value = true
  try {
    await Promise.all([loadStat(), loadBase(), loadTrend()])
  } finally {
    workerLoading.value = false
  }
}

onMounted(() => {
  loadAll()
})
</script>

<style scoped>
.stat-row {
  margin-bottom: 16px;
}
.stat-row .el-col {
  margin-bottom: 16px;
}
.chart-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 12px;
}
</style>
