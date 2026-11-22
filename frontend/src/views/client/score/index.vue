<template>
  <div class="page-container">
    <!-- 顶部筛选 -->
    <div class="page-card">
      <div class="search-bar">
        <span class="detail-label">统计周期：</span>
        <el-select v-model="months" style="width: 140px" @change="loadData">
          <el-option :value="3" label="近 3 个月" />
          <el-option :value="6" label="近 6 个月" />
          <el-option :value="12" label="近 12 个月" />
        </el-select>
        <el-button :icon="Refresh" @click="loadData">刷新</el-button>
      </div>
    </div>

    <!-- 评分趋势图 -->
    <div class="page-card">
      <div class="chart-title">评分趋势（近 {{ months }} 个月）</div>
      <BaseChart v-if="trend.length" :option="trendOption" height="340px" />
      <el-empty v-else description="暂无评分数据" />
    </div>

    <!-- 明细表 -->
    <div class="page-card">
      <div class="chart-title">月度明细</div>
      <el-table v-loading="loading" :data="trend" border stripe>
        <el-table-column prop="date" label="月份" min-width="120" />
        <el-table-column label="平均评分" min-width="160" align="center">
          <template #default="{ row }">
            <el-rate :model-value="Number(row.score) || 0" disabled allow-half />
            <span class="text-muted">{{ scoreText(row.score) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="count" label="修剪次数" min-width="120" align="center" />
        <template #empty>
          <el-empty description="暂无数据" />
        </template>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import BaseChart from '@/components/BaseChart.vue'
import { getMyTrend } from '@/api/trimRecord'
import { scoreText } from '@/utils'

const loading = ref(false)
const months = ref(6)
const trend = ref([])

// 加载评分趋势
async function loadData() {
  loading.value = true
  try {
    trend.value = (await getMyTrend({ months: months.value })) || []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

// 面积折线图：展示平均评分变化
const trendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 44, right: 24, top: 30, bottom: 40 },
  xAxis: { type: 'category', data: trend.value.map((i) => i.date), boundaryGap: false },
  yAxis: { type: 'value', min: 0, max: 5, name: '评分' },
  series: [
    {
      name: '平均评分',
      type: 'line',
      smooth: true,
      symbolSize: 8,
      areaStyle: { opacity: 0.18 },
      itemStyle: { color: '#2e7d32' },
      label: { show: true, formatter: (p) => scoreText(p.value) },
      data: trend.value.map((i) => i.score)
    }
  ]
}))
</script>

<style scoped>
.chart-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 12px;
}
</style>
